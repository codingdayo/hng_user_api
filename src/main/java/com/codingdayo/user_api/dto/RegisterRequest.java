package com.codingdayo.user_api.dto;

import com.codingdayo.user_api.model.Organisation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data

public class RegisterRequest {

        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String password;

        private List<Organisation> organisations;

        public RegisterRequest() {

        }
        //private List<Organisation> createdOrganisations;


}
