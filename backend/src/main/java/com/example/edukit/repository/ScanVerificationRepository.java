package com.example.edukit.repository;

import com.example.edukit.entity.ScanVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanVerificationRepository extends JpaRepository<ScanVerification, Long> {

    List<ScanVerification> findAllByOrderByVerifyTimeDesc();

    List<ScanVerification> findByKitIdOrderByVerifyTimeDesc(Long kitId);

}
