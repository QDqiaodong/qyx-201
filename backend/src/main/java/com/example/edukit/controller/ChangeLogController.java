package com.example.edukit.controller;

import com.example.edukit.dto.ChangeLogDTO;
import com.example.edukit.dto.ChangeRequestDTO;
import com.example.edukit.dto.MonthlySummaryDTO;
import com.example.edukit.service.ChangeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class ChangeLogController {

    private final ChangeLogService changeLogService;

    @PostMapping("/change")
    public ResponseEntity<ChangeLogDTO> changeResponsibleStaff(@RequestBody ChangeRequestDTO request) {
        return ResponseEntity.ok(changeLogService.changeResponsibleStaff(request));
    }

    @GetMapping
    public ResponseEntity<List<ChangeLogDTO>> getAllChangeLogs() {
        return ResponseEntity.ok(changeLogService.getAllChangeLogs());
    }

    @GetMapping("/kit/{kitId}")
    public ResponseEntity<List<ChangeLogDTO>> getChangeLogsByKit(@PathVariable Long kitId) {
        return ResponseEntity.ok(changeLogService.getChangeLogsByKit(kitId));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<ChangeLogDTO>> getChangeLogsByStaff(@PathVariable Long staffId) {
        return ResponseEntity.ok(changeLogService.getChangeLogsByStaff(staffId));
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySummaryDTO> getMonthlySummary(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ResponseEntity.ok(changeLogService.getMonthlySummary(year, month));
    }

    @GetMapping("/monthly/current")
    public ResponseEntity<MonthlySummaryDTO> getCurrentMonthSummary() {
        return ResponseEntity.ok(changeLogService.getCurrentMonthSummary());
    }

    @GetMapping("/monthly/all")
    public ResponseEntity<List<MonthlySummaryDTO>> getAllMonthlySummaries() {
        return ResponseEntity.ok(changeLogService.getAllMonthlySummaries());
    }

}
