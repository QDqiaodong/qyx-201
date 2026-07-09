package com.example.edukit.controller;

import com.example.edukit.dto.KitDTO;
import com.example.edukit.service.KitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kits")
@RequiredArgsConstructor
public class KitController {

    private final KitService kitService;

    @GetMapping
    public ResponseEntity<List<KitDTO>> getAllKits() {
        return ResponseEntity.ok(kitService.getAllKits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KitDTO> getKitById(@PathVariable Long id) {
        return ResponseEntity.ok(kitService.getKitById(id));
    }

    @PostMapping
    public ResponseEntity<KitDTO> createKit(@RequestBody KitDTO kitDTO) {
        return ResponseEntity.ok(kitService.createKit(kitDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KitDTO> updateKit(@PathVariable Long id, @RequestBody KitDTO kitDTO) {
        return ResponseEntity.ok(kitService.updateKit(id, kitDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKit(@PathVariable Long id) {
        kitService.deleteKit(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<KitDTO>> getKitsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(kitService.getKitsByCategory(category));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<KitDTO>> getKitsByStaff(@PathVariable Long staffId) {
        return ResponseEntity.ok(kitService.getKitsByStaff(staffId));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(kitService.getAllCategories());
    }

}
