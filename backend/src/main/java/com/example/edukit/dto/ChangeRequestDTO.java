package com.example.edukit.dto;

import lombok.Data;

@Data
public class ChangeRequestDTO {

    private Long kitId;
    private Long newStaffId;
    private String operator;
    private String reason;

}
