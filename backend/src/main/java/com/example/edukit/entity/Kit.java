package com.example.edukit.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "kit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kit_code", nullable = false, unique = true, length = 50)
    private String kitCode;

    /** 教具标签上的二维码值，更换责任人时按此码值核对档案 */
    @Column(name = "qr_code", unique = true, length = 100)
    private String qrCode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "adapted_classes", length = 200)
    private String adaptedClasses;

    @Column(name = "specification", length = 500)
    private String specification;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "status", length = 20)
    private String status = "IN_USE";

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_staff_id")
    private Staff responsibleStaff;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
