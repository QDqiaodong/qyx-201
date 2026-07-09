package com.example.edukit.service;

import com.example.edukit.dto.KitDTO;
import com.example.edukit.entity.Kit;
import com.example.edukit.entity.Staff;
import com.example.edukit.repository.KitRepository;
import com.example.edukit.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KitService {

    private final KitRepository kitRepository;
    private final StaffRepository staffRepository;

    public List<KitDTO> getAllKits() {
        return kitRepository.findAllWithStaff().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public KitDTO getKitById(Long id) {
        return kitRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("教具不存在: " + id));
    }

    @Transactional
    public KitDTO createKit(KitDTO kitDTO) {
        Kit kit = new Kit();
        kit.setKitCode(kitDTO.getKitCode());
        kit.setName(kitDTO.getName());
        kit.setCategory(kitDTO.getCategory());
        kit.setAdaptedClasses(kitDTO.getAdaptedClasses());
        kit.setSpecification(kitDTO.getSpecification());
        kit.setLocation(kitDTO.getLocation());
        kit.setStatus(kitDTO.getStatus() != null ? kitDTO.getStatus() : "IN_USE");
        kit.setImageUrl(kitDTO.getImageUrl());

        if (kitDTO.getResponsibleStaffId() != null) {
            Staff staff = staffRepository.findById(kitDTO.getResponsibleStaffId())
                    .orElseThrow(() -> new RuntimeException("教职工不存在: " + kitDTO.getResponsibleStaffId()));
            kit.setResponsibleStaff(staff);
        }

        Kit savedKit = kitRepository.save(kit);
        log.info("创建教具: {}", savedKit.getKitCode());
        return convertToDTO(savedKit);
    }

    @Transactional
    public KitDTO updateKit(Long id, KitDTO kitDTO) {
        Kit kit = kitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("教具不存在: " + id));

        kit.setKitCode(kitDTO.getKitCode());
        kit.setName(kitDTO.getName());
        kit.setCategory(kitDTO.getCategory());
        kit.setAdaptedClasses(kitDTO.getAdaptedClasses());
        kit.setSpecification(kitDTO.getSpecification());
        kit.setLocation(kitDTO.getLocation());
        if (kitDTO.getStatus() != null) {
            kit.setStatus(kitDTO.getStatus());
        }
        if (kitDTO.getImageUrl() != null) {
            kit.setImageUrl(kitDTO.getImageUrl());
        }

        if (kitDTO.getResponsibleStaffId() != null) {
            Staff staff = staffRepository.findById(kitDTO.getResponsibleStaffId())
                    .orElseThrow(() -> new RuntimeException("教职工不存在: " + kitDTO.getResponsibleStaffId()));
            kit.setResponsibleStaff(staff);
        }

        Kit updatedKit = kitRepository.save(kit);
        log.info("更新教具: {}", updatedKit.getKitCode());
        return convertToDTO(updatedKit);
    }

    @Transactional
    public void deleteKit(Long id) {
        Kit kit = kitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("教具不存在: " + id));
        kitRepository.delete(kit);
        log.info("删除教具: {}", kit.getKitCode());
    }

    public List<KitDTO> getKitsByCategory(String category) {
        return kitRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<KitDTO> getKitsByStaff(Long staffId) {
        return kitRepository.findByResponsibleStaffIdWithStaff(staffId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return kitRepository.findAllCategories();
    }

    private KitDTO convertToDTO(Kit kit) {
        KitDTO dto = new KitDTO();
        dto.setId(kit.getId());
        dto.setKitCode(kit.getKitCode());
        dto.setName(kit.getName());
        dto.setCategory(kit.getCategory());
        dto.setAdaptedClasses(kit.getAdaptedClasses());
        dto.setSpecification(kit.getSpecification());
        dto.setLocation(kit.getLocation());
        dto.setStatus(kit.getStatus());
        dto.setImageUrl(kit.getImageUrl());

        if (kit.getResponsibleStaff() != null) {
            dto.setResponsibleStaffId(kit.getResponsibleStaff().getId());
            dto.setResponsibleStaffName(kit.getResponsibleStaff().getName());
        }

        return dto;
    }

}
