package com.example.edukit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChangeLogDTO {

    private Long id;
    private Long kitId;
    private String kitCode;
    private String kitName;
    private Long oldStaffId;
    private String oldStaffName;
    private Long newStaffId;
    private String newStaffName;
    private LocalDateTime changeTime;
    private String operator;
    private String reason;

    /** 本次更换扫的是哪个码、谁核对的、什么时候核对的 */
    private Long verificationId;
    private String scannedCode;
    private String verifyOperator;
    private LocalDateTime verifyTime;

}
