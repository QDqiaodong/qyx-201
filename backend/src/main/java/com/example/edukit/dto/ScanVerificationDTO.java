package com.example.edukit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScanVerificationDTO {

    private Long id;
    private Long kitId;
    private String kitCode;
    private String kitName;
    private String scannedCode;
    private String operator;
    private Long expectedOldStaffId;
    private String expectedOldStaffName;
    private String result;
    private String failReason;
    private LocalDateTime verifyTime;
    private LocalDateTime expireTime;
    /** 是否已被一次更换消耗 */
    private Boolean consumed;

}
