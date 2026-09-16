package com.example.edukit.service;

import com.example.edukit.dto.ChangeLogDTO;
import com.example.edukit.entity.ChangeLog;
import com.example.edukit.entity.ScanVerification;
import org.springframework.stereotype.Component;

@Component
public class ChangeLogMapper {

    public ChangeLogDTO toDTO(ChangeLog changeLog) {
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

        ScanVerification verification = changeLog.getVerification();
        if (verification != null) {
            dto.setVerificationId(verification.getId());
            dto.setScannedCode(verification.getScannedCode());
            dto.setVerifyOperator(verification.getOperator());
            dto.setVerifyTime(verification.getVerifyTime());
        }

        return dto;
    }

}
