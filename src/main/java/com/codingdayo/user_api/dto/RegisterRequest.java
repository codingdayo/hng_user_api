package com.codingdayo.user_api.dto;

import com.codingdayo.user_api.model.Organisation;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data

public class RegisterRequest {

        private Long userId;

        @NotBlank(message = "Name is required")
        private String firstName;

        @NotBlank(message = "Name is required")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Column(unique = true)
        private String email;

        @NotBlank(message = "Phone Number is required")
        private String phone;

        @NotBlank(message = "Password is required")
        private String password;

        private List<Organisation> organisations;

        public RegisterRequest() {

        }
        //private List<Organisation> createdOrganisations;


}
