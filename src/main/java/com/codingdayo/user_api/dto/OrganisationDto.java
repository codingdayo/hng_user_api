package com.codingdayo.user_api.dto;

import lombok.Data;

@Data
public class OrganisationDto {
    private Long orgId;
    private String name;
    private String description;


    public OrganisationDto(Long orgId, String name, String description) {
        this.orgId = orgId;
        this.name = name;
        this.description = description;
    }
}
