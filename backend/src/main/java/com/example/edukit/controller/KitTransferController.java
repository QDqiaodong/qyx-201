package com.example.edukit.controller;

import com.example.edukit.dto.ChangeLogDTO;
import com.example.edukit.dto.ScanVerificationDTO;
import com.example.edukit.dto.ScanVerifyRequestDTO;
import com.example.edukit.dto.TransferConfirmRequestDTO;
import com.example.edukit.service.KitTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教具责任人更换：先扫码核对（verify），后登记更换（confirm）。
 */
@RestController
@RequestMapping("/api/kit-transfers")
@RequiredArgsConstructor
public class KitTransferController {

    private final KitTransferService kitTransferService;

    @PostMapping("/verify")
    public ResponseEntity<ScanVerificationDTO> verify(@RequestBody ScanVerifyRequestDTO request) {
        return ResponseEntity.ok(kitTransferService.verify(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ChangeLogDTO> confirm(@RequestBody TransferConfirmRequestDTO request) {
        return ResponseEntity.ok(kitTransferService.confirm(request));
    }

    @GetMapping("/verifications")
    public ResponseEntity<List<ScanVerificationDTO>> getAllVerifications() {
        return ResponseEntity.ok(kitTransferService.getAllVerifications());
    }

    @GetMapping("/verifications/kit/{kitId}")
    public ResponseEntity<List<ScanVerificationDTO>> getVerificationsByKit(@PathVariable Long kitId) {
        return ResponseEntity.ok(kitTransferService.getVerificationsByKit(kitId));
    }

}
