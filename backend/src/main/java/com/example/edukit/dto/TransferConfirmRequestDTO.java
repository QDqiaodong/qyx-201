package com.example.edukit.dto;

import lombok.Data;

/**
 * 登记更换请求：更换责任人第二步，必须凭扫码核对通过的核对记录提交。
 */
@Data
public class TransferConfirmRequestDTO {

    /** 扫码核对通过的核对记录 id */
    private Long verificationId;

    private Long newStaffId;

    private String reason;

}
