package com.example.edukit.repository;

import com.example.edukit.entity.Kit;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {

    Optional<Kit> findByKitCode(String kitCode);

    Optional<Kit> findByQrCode(String qrCode);

    /** 登记更换时锁定教具行，串行化同一教具的并发更换 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM Kit k WHERE k.id = :id")
    Optional<Kit> findByIdForUpdate(@Param("id") Long id);

    List<Kit> findByCategory(String category);

    List<Kit> findByResponsibleStaffId(Long staffId);

    List<Kit> findByStatus(String status);

    @Query("SELECT k FROM Kit k LEFT JOIN FETCH k.responsibleStaff ORDER BY k.kitCode")
    List<Kit> findAllWithStaff();

    @Query("SELECT k FROM Kit k LEFT JOIN FETCH k.responsibleStaff WHERE k.responsibleStaff.id = :staffId")
    List<Kit> findByResponsibleStaffIdWithStaff(Long staffId);

    @Query("SELECT DISTINCT k.category FROM Kit k ORDER BY k.category")
    List<String> findAllCategories();

}
