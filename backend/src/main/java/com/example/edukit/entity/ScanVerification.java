package com.example.edukit.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 扫码核对记录。
 * 每一次扫码核对（无论成败）都落一条记录，作为审计痕迹只插入、不更新、不删除。
 * 核对是否被用于更换，通过 change_log.verification_id 是否引用本条记录来判断。
 */
@Entity
@Table(name = "scan_verification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanVerification {

    /** 核对通过 */
    public static final String RESULT_SUCCESS = "SUCCESS";
    /** 码值无法识别，未找到对应教具档案 */
    public static final String RESULT_FAIL_CODE_UNKNOWN = "FAIL_CODE_UNKNOWN";
    /** 扫入的码属于其他教具 */
    public static final String RESULT_FAIL_CODE_MISMATCH = "FAIL_CODE_MISMATCH";
    /** 教具已不在原责任人名下 */
    public static final String RESULT_FAIL_OWNER_CHANGED = "FAIL_OWNER_CHANGED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_id", nullable = false)
    private Kit kit;

    @Column(name = "kit_code", nullable = false, length = 50)
    private String kitCode;

    @Column(name = "kit_name", length = 100)
    private String kitName;

    /** 扫进来的码值 */
    @Column(name = "scanned_code", nullable = false, length = 100)
    private String scannedCode;

    /** 核对人 */
    @Column(name = "operator", nullable = false, length = 50)
    private String operator;

    /** 核对那一刻登记在册的责任人快照 */
    @Column(name = "expected_old_staff_id")
    private Long expectedOldStaffId;

    @Column(name = "expected_old_staff_name", length = 50)
    private String expectedOldStaffName;

    /** 核对结果 */
    @Column(name = "result", nullable = false, length = 30)
    private String result;

    @Column(name = "fail_reason", length = 500)
    private String failReason;

    /** 核对时间 */
    @Column(name = "verify_time", nullable = false)
    private LocalDateTime verifyTime;

    /** 核对凭证有效期（仅核对通过时有值），超时需重新扫码 */
    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    @PrePersist
    protected void onCreate() {
        if (verifyTime == null) {
            verifyTime = LocalDateTime.now();
        }
    }

}
