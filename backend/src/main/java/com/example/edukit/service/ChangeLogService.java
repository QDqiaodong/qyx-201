package com.example.edukit.service;

import com.example.edukit.dto.ChangeLogDTO;
import com.example.edukit.dto.ChangeRequestDTO;
import com.example.edukit.dto.MonthlySummaryDTO;
import com.example.edukit.entity.ChangeLog;
import com.example.edukit.entity.Kit;
import com.example.edukit.entity.Staff;
import com.example.edukit.repository.ChangeLogRepository;
import com.example.edukit.repository.KitRepository;
import com.example.edukit.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final KitRepository kitRepository;
    private final StaffRepository staffRepository;

    @Transactional
    public ChangeLogDTO changeResponsibleStaff(ChangeRequestDTO request) {
        Kit kit = kitRepository.findById(request.getKitId())
                .orElseThrow(() -> new RuntimeException("教具不存在: " + request.getKitId()));

        Staff newStaff = staffRepository.findById(request.getNewStaffId())
                .orElseThrow(() -> new RuntimeException("新责任人不存在: " + request.getNewStaffId()));

        Staff oldStaff = kit.getResponsibleStaff();

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
        changeLog.setOperator(request.getOperator());
        changeLog.setReason(request.getReason());

        ChangeLog savedLog = changeLogRepository.save(changeLog);

        kit.setResponsibleStaff(newStaff);
        kitRepository.save(kit);

        saveChangeLogToFile(savedLog);

        log.info("责任人变更: 教具[{}] 从[{}]变更为[{}], 操作人: {}",
                kit.getKitCode(),
                oldStaff != null ? oldStaff.getName() : "无",
                newStaff.getName(),
                request.getOperator());

        return convertToDTO(savedLog);
    }

    private void saveChangeLogToFile(ChangeLog changeLog) {
        String directory = "/app/data/changelogs";
        try {
            java.io.File dir = new java.io.File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String dateStr = changeLog.getChangeTime().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String fileName = directory + "/" + dateStr + ".log";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                String logLine = String.format("[%s] 教具:%s(%s) | 旧责任人:%s | 新责任人:%s | 操作人:%s | 原因:%s%n",
                        changeLog.getChangeTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        changeLog.getKitCode(),
                        changeLog.getKitName(),
                        changeLog.getOldStaffName() != null ? changeLog.getOldStaffName() : "无",
                        changeLog.getNewStaffName(),
                        changeLog.getOperator(),
                        changeLog.getReason() != null ? changeLog.getReason() : "无");
                writer.write(logLine);
            }
        } catch (IOException e) {
            log.error("保存变更日志文件失败", e);
        }
    }

    public List<ChangeLogDTO> getAllChangeLogs() {
        return changeLogRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ChangeLogDTO> getChangeLogsByKit(Long kitId) {
        return changeLogRepository.findByKitId(kitId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ChangeLogDTO> getChangeLogsByStaff(Long staffId) {
        return changeLogRepository.findByStaffInvolved(staffId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MonthlySummaryDTO getMonthlySummary(Integer year, Integer month) {
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endTime = startTime.plusMonths(1).minusNanos(1);

        List<ChangeLog> logs = changeLogRepository.findByMonth(startTime, endTime);

        MonthlySummaryDTO summary = new MonthlySummaryDTO();
        summary.setYear(year);
        summary.setMonth(month);
        summary.setTotalChanges(logs.size());
        summary.setChangeLogs(logs.stream().map(this::convertToDTO).collect(Collectors.toList()));

        return summary;
    }

    public List<MonthlySummaryDTO> getAllMonthlySummaries() {
        List<Object[]> months = changeLogRepository.findDistinctMonths();
        List<MonthlySummaryDTO> summaries = new ArrayList<>();

        for (Object[] row : months) {
            Integer year = ((Number) row[0]).intValue();
            Integer month = ((Number) row[1]).intValue();
            summaries.add(getMonthlySummary(year, month));
        }

        return summaries;
    }

    public MonthlySummaryDTO getCurrentMonthSummary() {
        LocalDate now = LocalDate.now();
        return getMonthlySummary(now.getYear(), now.getMonthValue());
    }

    private ChangeLogDTO convertToDTO(ChangeLog changeLog) {
        ChangeLogDTO dto = new ChangeLogDTO();
        dto.setId(changeLog.getId());
        dto.setKitId(changeLog.getKit().getId());
        dto.setKitCode(changeLog.getKitCode());
        dto.setKitName(changeLog.getKitName());

        if (changeLog.getOldStaff() != null) {
            dto.setOldStaffId(changeLog.getOldStaff().getId());
        }
        dto.setOldStaffName(changeLog.getOldStaffName());

        dto.setNewStaffId(changeLog.getNewStaff().getId());
        dto.setNewStaffName(changeLog.getNewStaffName());

        dto.setChangeTime(changeLog.getChangeTime());
        dto.setOperator(changeLog.getOperator());
        dto.setReason(changeLog.getReason());

        return dto;
    }

}
