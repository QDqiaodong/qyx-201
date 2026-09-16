package com.example.edukit.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "change_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_id", nullable = false)
    private Kit kit;

    @Column(name = "kit_code", length = 50)
    private String kitCode;

    @Column(name = "kit_name", length = 100)
    private String kitName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_staff_id")
    private Staff oldStaff;

    @Column(name = "old_staff_name", length = 50)
    private String oldStaffName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_staff_id", nullable = false)
    private Staff newStaff;

    @Column(name = "new_staff_name", length = 50)
    private String newStaffName;

    @Column(name = "change_time", nullable = false)
    private LocalDateTime changeTime;

    @Column(name = "operator", nullable = false, length = 50)
    private String operator;

    @Column(name = "reason", length = 500)
    private String reason;

    /** 本次更换依据的扫码核对记录；唯一约束保证一条核对记录只能被消耗一次 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verification_id", unique = true)
    private ScanVerification verification;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (changeTime == null) {
            changeTime = LocalDateTime.now();
        }
    }

}
