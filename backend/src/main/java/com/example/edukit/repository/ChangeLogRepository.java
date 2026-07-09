package com.example.edukit.repository;

import com.example.edukit.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    List<ChangeLog> findByKitId(Long kitId);

    List<ChangeLog> findByNewStaffId(Long staffId);

    List<ChangeLog> findByOldStaffId(Long staffId);

    @Query("SELECT cl FROM ChangeLog cl WHERE cl.changeTime BETWEEN :startTime AND :endTime ORDER BY cl.changeTime DESC")
    List<ChangeLog> findByMonth(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT cl FROM ChangeLog cl WHERE cl.newStaff.id = :staffId OR cl.oldStaff.id = :staffId ORDER BY cl.changeTime DESC")
    List<ChangeLog> findByStaffInvolved(Long staffId);

    @Query("SELECT COUNT(cl) FROM ChangeLog cl WHERE cl.changeTime BETWEEN :startTime AND :endTime")
    Long countByMonth(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT DISTINCT FUNCTION('YEAR', cl.changeTime) as year, FUNCTION('MONTH', cl.changeTime) as month " +
           "FROM ChangeLog cl ORDER BY year DESC, month DESC")
    List<Object[]> findDistinctMonths();

}
