package com.example.edukit.dto;

import lombok.Data;

/**
 * 扫码核对请求：更换责任人第一步，把教具标签上的码扫进来核对。
 */
@Data
public class ScanVerifyRequestDTO {

    /** 要更换责任人的教具 */
    private Long kitId;

    /** 扫进来的码值 */
    private String qrCode;

    /** 核对人 */
    private String operator;

    /** 页面上看到的当前责任人，用于发现"人已换走"的乱序 */
    private Long expectedOldStaffId;

}
