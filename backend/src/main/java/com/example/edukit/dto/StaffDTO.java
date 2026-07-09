package com.example.edukit.dto;

import lombok.Data;

@Data
public class StaffDTO {

    private Long id;
    private String staffCode;
    private String name;
    private String department;
    private String position;
    private String phone;
    private String email;
    private String status;

}
