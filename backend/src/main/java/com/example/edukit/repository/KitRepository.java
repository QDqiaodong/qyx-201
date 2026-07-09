package com.example.edukit.repository;

import com.example.edukit.entity.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {

    Optional<Kit> findByKitCode(String kitCode);

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
