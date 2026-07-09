package com.example.edukit.service;

import com.example.edukit.dto.StaffDTO;
import com.example.edukit.entity.Staff;
import com.example.edukit.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffService {

    private final StaffRepository staffRepository;

    public List<StaffDTO> getAllStaff() {
        return staffRepository.findAllByOrderByName().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StaffDTO getStaffById(Long id) {
        return staffRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("教职工不存在: " + id));
    }

    @Transactional
    public StaffDTO createStaff(StaffDTO staffDTO) {
        Staff staff = new Staff();
        staff.setStaffCode(staffDTO.getStaffCode());
        staff.setName(staffDTO.getName());
        staff.setDepartment(staffDTO.getDepartment());
        staff.setPosition(staffDTO.getPosition());
        staff.setPhone(staffDTO.getPhone());
        staff.setEmail(staffDTO.getEmail());
        staff.setStatus(staffDTO.getStatus() != null ? staffDTO.getStatus() : "ACTIVE");

        Staff savedStaff = staffRepository.save(staff);
        log.info("创建教职工: {}", savedStaff.getName());
        return convertToDTO(savedStaff);
    }

    @Transactional
    public StaffDTO updateStaff(Long id, StaffDTO staffDTO) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("教职工不存在: " + id));

        staff.setStaffCode(staffDTO.getStaffCode());
        staff.setName(staffDTO.getName());
        staff.setDepartment(staffDTO.getDepartment());
        staff.setPosition(staffDTO.getPosition());
        staff.setPhone(staffDTO.getPhone());
        staff.setEmail(staffDTO.getEmail());
        if (staffDTO.getStatus() != null) {
            staff.setStatus(staffDTO.getStatus());
        }

        Staff updatedStaff = staffRepository.save(staff);
        log.info("更新教职工: {}", updatedStaff.getName());
        return convertToDTO(updatedStaff);
    }

    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("教职工不存在: " + id));
        staffRepository.delete(staff);
        log.info("删除教职工: {}", staff.getName());
    }

    public List<StaffDTO> searchStaffByName(String name) {
        return staffRepository.findByNameContaining(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StaffDTO> getStaffByDepartment(String department) {
        return staffRepository.findByDepartment(department).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private StaffDTO convertToDTO(Staff staff) {
        StaffDTO dto = new StaffDTO();
        dto.setId(staff.getId());
        dto.setStaffCode(staff.getStaffCode());
        dto.setName(staff.getName());
        dto.setDepartment(staff.getDepartment());
        dto.setPosition(staff.getPosition());
        dto.setPhone(staff.getPhone());
        dto.setEmail(staff.getEmail());
        dto.setStatus(staff.getStatus());
        return dto;
    }

}
