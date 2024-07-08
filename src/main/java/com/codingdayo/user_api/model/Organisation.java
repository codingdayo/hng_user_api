package com.codingdayo.user_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "organisations")
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orgId;

    @NotBlank(message = "Organisation name is required")
    private String name;

    @NotBlank(message = "Name is required")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;


}
