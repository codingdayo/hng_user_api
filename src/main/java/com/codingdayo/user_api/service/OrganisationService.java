package com.codingdayo.user_api.service;

import com.codingdayo.user_api.dto.OrganisationRequest;
import com.codingdayo.user_api.dto.RegisterRequest;
import com.codingdayo.user_api.dto.Response;
import com.codingdayo.user_api.model.Organisation;
import com.codingdayo.user_api.model.User;

import java.util.List;

public interface OrganisationService {

        Response addNewOrg(OrganisationRequest organisationRequest);

        Response findAllOrg();

        Response findByOrgId(Long orgID);

        //Response addNewUser(Long orgId, RegisterRequest registerRequest);

        Response getUserById(Long userId);

        Response addByUserId(Long orgId, String userId);

}
