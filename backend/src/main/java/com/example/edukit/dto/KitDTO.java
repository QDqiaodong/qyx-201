package com.example.edukit.dto;

import lombok.Data;

@Data
public class KitDTO {

    private Long id;
    private String kitCode;
    private String name;
    private String category;
    private String adaptedClasses;
    private String specification;
    private String location;
    private String status;
    private String imageUrl;
    private Long responsibleStaffId;
    private String responsibleStaffName;

}
