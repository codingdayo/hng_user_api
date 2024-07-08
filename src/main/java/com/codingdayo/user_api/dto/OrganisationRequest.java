package com.codingdayo.user_api.dto;

import com.codingdayo.user_api.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class OrganisationRequest {

    private Long orgId;
    private String name;
    private String description;
    private List<User> users;
    private User creator;
}
