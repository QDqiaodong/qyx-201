package com.example.edukit.service;

import com.example.edukit.dto.ChangeLogDTO;
import com.example.edukit.dto.ScanVerificationDTO;
import com.example.edukit.dto.ScanVerifyRequestDTO;
import com.example.edukit.dto.TransferConfirmRequestDTO;
import com.example.edukit.entity.ChangeLog;
import com.example.edukit.entity.Kit;
import com.example.edukit.entity.ScanVerification;
import com.example.edukit.entity.Staff;
import com.example.edukit.repository.ChangeLogRepository;
import com.example.edukit.repository.KitRepository;
import com.example.edukit.repository.ScanVerificationRepository;
import com.example.edukit.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 教具责任人更换：先扫码核对，后登记更换。
 *
 * 第一步 verify：把教具标签上的码扫进来，按码值找到教具档案并核对，
 *                核对通过生成一条限时、一次性的核对记录。
 * 第二步 confirm：凭核对记录登记更换。校验核对记录、锁定教具、确认责任人未变、
 *                写变更日志（消耗核对）、更新责任人，全部在同一事务内完成，
 *                任何一步失败整体回滚，不留半截状态。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KitTransferService {

    /** 扫码核对凭证有效分钟数，超时需重新扫码 */
    private static final int VERIFY_VALID_MINUTES = 5;

    private final KitRepository kitRepository;
    private final StaffRepository staffRepository;
    private final ChangeLogRepository changeLogRepository;
    private final ScanVerificationRepository scanVerificationRepository;
    private final ScanVerificationRecorder scanVerificationRecorder;
    private final ChangeLogMapper changeLogMapper;

    /**
     * 第一步：扫码核对。核对失败会留下失败痕迹并说明原因（码不对 / 已不在原责任人名下）。
     */
    @Transactional
    public ScanVerificationDTO verify(ScanVerifyRequestDTO request) {
        if (request.getKitId() == null) {
            throw new RuntimeException("请先选择要更换责任人的教具");
        }
        if (!StringUtils.hasText(request.getQrCode())) {
            throw new RuntimeException("请先扫码：把这件教具标签上的码扫进来再核对");
        }
        if (!StringUtils.hasText(request.getOperator())) {
            throw new RuntimeException("请填写核对人");
        }

        Kit target = kitRepository.findById(request.getKitId())
                .orElseThrow(() -> new RuntimeException("教具不存在: " + request.getKitId()));

        String scannedCode = request.getQrCode().trim();
        String operator = request.getOperator().trim();
        Staff currentStaff = target.getResponsibleStaff();
        Long currentStaffId = currentStaff != null ? currentStaff.getId() : null;
        String currentStaffName = currentStaff != null ? currentStaff.getName() : null;

        Optional<Kit> scannedKit = kitRepository.findByQrCode(scannedCode);
        if (scannedKit.isEmpty()) {
            scanVerificationRecorder.record(target, scannedCode, operator,
                    currentStaffId, currentStaffName,
                    ScanVerification.RESULT_FAIL_CODE_UNKNOWN,
                    "码值无法识别，未找到对应教具档案", null);
            throw new RuntimeException("扫码核对失败：码值无法识别，未找到对应教具档案，请确认扫的是本教具标签上的二维码");
        }

        if (!scannedKit.get().getId().equals(target.getId())) {
            Kit other = scannedKit.get();
            scanVerificationRecorder.record(target, scannedCode, operator,
                    currentStaffId, currentStaffName,
                    ScanVerification.RESULT_FAIL_CODE_MISMATCH,
                    "扫入的码属于教具「" + other.getKitCode() + "」", null);
            throw new RuntimeException("扫码核对失败：扫入的码属于教具「" + other.getKitCode() + " " + other.getName()
                    + "」，与要更换的教具「" + target.getKitCode() + " " + target.getName() + "」对不上");
        }

        if (request.getExpectedOldStaffId() != null
                && !Objects.equals(request.getExpectedOldStaffId(), currentStaffId)) {
            String reason = "该教具已不在原责任人名下，当前责任人为「"
                    + (currentStaffName != null ? currentStaffName : "无") + "」";
            scanVerificationRecorder.record(target, scannedCode, operator,
                    currentStaffId, currentStaffName,
                    ScanVerification.RESULT_FAIL_OWNER_CHANGED, reason, null);
            throw new RuntimeException("扫码核对失败：" + reason + "，请刷新确认后再操作");
        }

        ScanVerification verification = scanVerificationRecorder.record(target, scannedCode, operator,
                currentStaffId, currentStaffName,
                ScanVerification.RESULT_SUCCESS, null,
                LocalDateTime.now().plusMinutes(VERIFY_VALID_MINUTES));

        log.info("扫码核对通过: 教具[{}] 核对人[{}] 核对记录[{}]", target.getKitCode(), operator, verification.getId());
        return convertToVerificationDTO(verification, false);
    }

    /**
     * 第二步：凭核对记录登记更换。同一事务内完成全部写入，失败整体回滚。
     */
    @Transactional
    public ChangeLogDTO confirm(TransferConfirmRequestDTO request) {
        if (request.getVerificationId() == null) {
            throw new RuntimeException("未扫码核对：请先把这件教具的码扫进来核对，核对通过后才能登记更换");
        }
        ScanVerification verification = scanVerificationRepository.findById(request.getVerificationId())
                .orElseThrow(() -> new RuntimeException("核对记录不存在，请先扫码核对"));

        // 锁定教具行，串行化同一教具的并发更换：后到的事务等前一个提交后再校验
        Kit kit = kitRepository.findByIdForUpdate(verification.getKit().getId())
                .orElseThrow(() -> new RuntimeException("教具不存在"));

        if (!ScanVerification.RESULT_SUCCESS.equals(verification.getResult())) {
            throw new RuntimeException("该次扫码核对未通过，不能用于更换，请重新扫码核对");
        }
        if (changeLogRepository.existsByVerificationId(verification.getId())) {
            throw new RuntimeException("该核对记录已用于一次更换，不能重复使用，请重新扫码核对");
        }
        if (verification.getExpireTime() != null && LocalDateTime.now().isAfter(verification.getExpireTime())) {
            throw new RuntimeException("核对结果已超时失效，请重新扫码核对");
        }

        Staff oldStaff = kit.getResponsibleStaff();
        Long currentStaffId = oldStaff != null ? oldStaff.getId() : null;
        if (!Objects.equals(currentStaffId, verification.getExpectedOldStaffId())) {
            throw new RuntimeException("该教具已不在原责任人「"
                    + (verification.getExpectedOldStaffName() != null ? verification.getExpectedOldStaffName() : "无")
                    + "」名下（当前责任人：" + (oldStaff != null ? oldStaff.getName() : "无")
                    + "），可能已被他人转走，本次更换不能成立，请重新扫码核对");
        }

        if (request.getNewStaffId() == null) {
            throw new RuntimeException("请选择新责任人");
        }
        Staff newStaff = staffRepository.findById(request.getNewStaffId())
                .orElseThrow(() -> new RuntimeException("新责任人不存在: " + request.getNewStaffId()));
        if (Objects.equals(currentStaffId, newStaff.getId())) {
            throw new RuntimeException("新责任人与当前责任人相同，无需更换");
        }

        ChangeLog changeLog = new ChangeLog();
        changeLog.setKit(kit);
        changeLog.setKitCode(kit.getKitCode());
        changeLog.setKitName(kit.getName());
        if (oldStaff != null) {
            changeLog.setOldStaff(oldStaff);
            changeLog.setOldStaffName(oldStaff.getName());
        }
        changeLog.setNewStaff(newStaff);
        changeLog.setNewStaffName(newStaff.getName());
        changeLog.setChangeTime(LocalDateTime.now());
        changeLog.setOperator(verification.getOperator());
        changeLog.setReason(request.getReason());
        changeLog.setVerification(verification);
        ChangeLog savedLog = changeLogRepository.save(changeLog);

        kit.setResponsibleStaff(newStaff);
        kitRepository.save(kit);

        // 文件台账在事务提交后再写，避免更换失败却留下已更换的记录
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                saveChangeLogToFile(savedLog, verification);
            }
        });

        log.info("责任人变更: 教具[{}] 从[{}]变更为[{}], 核对人[{}] 核对记录[{}]",
                kit.getKitCode(),
                oldStaff != null ? oldStaff.getName() : "无",
                newStaff.getName(),
                verification.getOperator(),
                verification.getId());

        return changeLogMapper.toDTO(savedLog);
    }

    @Transactional(readOnly = true)
    public List<ScanVerificationDTO> getAllVerifications() {
        Set<Long> consumedIds = new HashSet<>(changeLogRepository.findVerificationIds());
        return scanVerificationRepository.findAllByOrderByVerifyTimeDesc().stream()
                .map(v -> convertToVerificationDTO(v, consumedIds.contains(v.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScanVerificationDTO> getVerificationsByKit(Long kitId) {
        Set<Long> consumedIds = new HashSet<>(changeLogRepository.findVerificationIds());
        return scanVerificationRepository.findByKitIdOrderByVerifyTimeDesc(kitId).stream()
                .map(v -> convertToVerificationDTO(v, consumedIds.contains(v.getId())))
                .collect(Collectors.toList());
    }

    private ScanVerificationDTO convertToVerificationDTO(ScanVerification verification, boolean consumed) {
        ScanVerificationDTO dto = new ScanVerificationDTO();
        dto.setId(verification.getId());
        dto.setKitId(verification.getKit().getId());
        dto.setKitCode(verification.getKitCode());
        dto.setKitName(verification.getKitName());
        dto.setScannedCode(verification.getScannedCode());
        dto.setOperator(verification.getOperator());
        dto.setExpectedOldStaffId(verification.getExpectedOldStaffId());
        dto.setExpectedOldStaffName(verification.getExpectedOldStaffName());
        dto.setResult(verification.getResult());
        dto.setFailReason(verification.getFailReason());
        dto.setVerifyTime(verification.getVerifyTime());
        dto.setExpireTime(verification.getExpireTime());
        dto.setConsumed(consumed);
        return dto;
    }

    private void saveChangeLogToFile(ChangeLog changeLog, ScanVerification verification) {
        String directory = "/app/data/changelogs";
        try {
            java.io.File dir = new java.io.File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String dateStr = changeLog.getChangeTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String fileName = directory + "/" + dateStr + ".log";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                String logLine = String.format("[%s] 教具:%s(%s) | 旧责任人:%s | 新责任人:%s | 操作人:%s | 扫码码值:%s | 核对人:%s | 核对时间:%s | 原因:%s%n",
                        changeLog.getChangeTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        changeLog.getKitCode(),
                        changeLog.getKitName(),
                        changeLog.getOldStaffName() != null ? changeLog.getOldStaffName() : "无",
                        changeLog.getNewStaffName(),
                        changeLog.getOperator(),
                        verification.getScannedCode(),
                        verification.getOperator(),
                        verification.getVerifyTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        changeLog.getReason() != null ? changeLog.getReason() : "无");
                writer.write(logLine);
            }
        } catch (IOException e) {
            log.error("保存变更日志文件失败", e);
        }
    }

}
