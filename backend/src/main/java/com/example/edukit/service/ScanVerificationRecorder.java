package com.example.edukit.service;

import com.example.edukit.entity.Kit;
import com.example.edukit.entity.ScanVerification;
import com.example.edukit.repository.ScanVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 扫码核对记录落库。
 * 核对记录是审计痕迹，只插入、不更新、不删除；
 * 使用独立事务，保证核对失败的痕迹在业务异常回滚后依然保留。
 */
@Service
@RequiredArgsConstructor
public class ScanVerificationRecorder {

    private final ScanVerificationRepository scanVerificationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ScanVerification record(Kit kit, String scannedCode, String operator,
                                   Long expectedOldStaffId, String expectedOldStaffName,
                                   String result, String failReason, LocalDateTime expireTime) {
        ScanVerification verification = new ScanVerification();
        verification.setKit(kit);
        verification.setKitCode(kit.getKitCode());
        verification.setKitName(kit.getName());
        verification.setScannedCode(scannedCode);
        verification.setOperator(operator);
        verification.setExpectedOldStaffId(expectedOldStaffId);
        verification.setExpectedOldStaffName(expectedOldStaffName);
        verification.setResult(result);
        verification.setFailReason(failReason);
        verification.setVerifyTime(LocalDateTime.now());
        verification.setExpireTime(expireTime);
        return scanVerificationRepository.save(verification);
    }

}
