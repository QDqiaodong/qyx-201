package com.example.edukit.service;

import com.example.edukit.dto.ChangeLogDTO;
import com.example.edukit.dto.MonthlySummaryDTO;
import com.example.edukit.entity.ChangeLog;
import com.example.edukit.repository.ChangeLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogMapper changeLogMapper;

    @Transactional(readOnly = true)
    public List<ChangeLogDTO> getAllChangeLogs() {
        return changeLogRepository.findAll().stream()
                .map(changeLogMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChangeLogDTO> getChangeLogsByKit(Long kitId) {
        return changeLogRepository.findByKitId(kitId).stream()
                .map(changeLogMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChangeLogDTO> getChangeLogsByStaff(Long staffId) {
        return changeLogRepository.findByStaffInvolved(staffId).stream()
                .map(changeLogMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MonthlySummaryDTO getMonthlySummary(Integer year, Integer month) {
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endTime = startTime.plusMonths(1).minusNanos(1);

        List<ChangeLog> logs = changeLogRepository.findByMonth(startTime, endTime);

        MonthlySummaryDTO summary = new MonthlySummaryDTO();
        summary.setYear(year);
        summary.setMonth(month);
        summary.setTotalChanges(logs.size());
        summary.setChangeLogs(logs.stream().map(changeLogMapper::toDTO).collect(Collectors.toList()));

        return summary;
    }

    @Transactional(readOnly = true)
    public List<MonthlySummaryDTO> getAllMonthlySummaries() {
        List<Object[]> months = changeLogRepository.findDistinctMonths();
        List<MonthlySummaryDTO> summaries = new ArrayList<>();

        for (Object[] row : months) {
            Integer year = ((Number) row[0]).intValue();
            Integer month = ((Number) row[1]).intValue();
            summaries.add(getMonthlySummary(year, month));
        }

        return summaries;
    }

    @Transactional(readOnly = true)
    public MonthlySummaryDTO getCurrentMonthSummary() {
        LocalDate now = LocalDate.now();
        return getMonthlySummary(now.getYear(), now.getMonthValue());
    }

}
