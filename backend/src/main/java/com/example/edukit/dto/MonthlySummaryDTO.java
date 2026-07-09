package com.example.edukit.dto;

import lombok.Data;

import java.util.List;

@Data
public class MonthlySummaryDTO {

    private Integer year;
    private Integer month;
    private Integer totalChanges;
    private List<ChangeLogDTO> changeLogs;

}
