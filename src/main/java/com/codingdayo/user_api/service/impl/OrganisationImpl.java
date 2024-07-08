package com.codingdayo.user_api.service.impl;

import com.codingdayo.user_api.dto.OrganisationDto;
import com.codingdayo.user_api.dto.OrganisationRequest;
import com.codingdayo.user_api.dto.RegisterRequest;
import com.codingdayo.user_api.dto.Response;
import com.codingdayo.user_api.exceptions.OurException;
import com.codingdayo.user_api.model.Organisation;
import com.codingdayo.user_api.model.User;
import com.codingdayo.user_api.repository.OrganisationRepository;
import com.codingdayo.user_api.repository.UserRepository;
import com.codingdayo.user_api.service.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganisationImpl implements OrganisationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public Response addNewOrg(OrganisationRequest organisationRequest) {
        Response response = new Response();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("User not found"));

            Organisation organisation = new Organisation();

            organisation.setName(organisationRequest.getName());
            organisation.setDescription(organisationRequest.getDescription());
            organisation.setCreator(user);
            organisation.setUsers(List.of(user));

            Organisation savedOrg = organisationRepository.save(organisation);

            response.setStatusCode(201);
            response.setStatus("success");
            response.setMessage("Organisation created successfully");
            response.setData(Map.of(
                    "orgId", savedOrg.getOrgId().toString(),
                    "firstName", savedOrg.getName(),
                    "lastName", savedOrg.getDescription()
            ));

        } catch (OurException e) {
            response.setStatus("Bad Request");
            response.setMessage("Client error");
            response.setStatusCode(400);
        }
        return response;
    }

    @Override
    public Response findAllOrg() {
        Response response = new Response();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {

            User user = userRepository.findByEmail(email).orElseThrow(() -> new OurException("User not found"));

            List<Organisation> organisationList = organisationRepository.findByUser(user.getUserId(), user);


            List<OrganisationDto> organisationDTOs = organisationList.stream()
                    .map(org -> new OrganisationDto(org.getOrgId(), org.getName(), org.getDescription()))
                    .collect(Collectors.toList());

            response.setStatusCode(200);
            response.setStatus("success");
            response.setMessage("All Organisations");
            response.setData(Map.of(
                            "organisations", organisationDTOs));

        }catch (OurException e) {
            response.setStatus("Bad Request");
            response.setMessage("Client error");
            response.setStatusCode(400);
        }
        return response;
    }

    @Override
    public Response findByOrgId(Long orgId) {
        Response response = new Response();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try{

            User requestingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new OurException("Requesting user not found"));


            Organisation organisation = organisationRepository.findById(orgId).orElseThrow(() -> new OurException("Organisation not Found"));


            boolean isAuthorized = organisation.getCreator().getUserId().equals(requestingUser.getUserId()) ||
                    organisation.getUsers().contains(requestingUser);

            if (!isAuthorized) {
                throw new OurException("Not authorized to view this organisation");
            }

            response.setStatusCode(200);
            response.setStatus("success");
            response.setMessage("Organisation " + organisation.getOrgId());
            response.setData(Map.of(
                    "orgId", organisation.getOrgId(),
                    "name", organisation.getName(),
                    "description", organisation.getDescription()
            ));
                    //"organisations", organisation

        }catch (OurException e) {
            response.setStatus("Bad Request");
            response.setMessage("Not authorised to get this organisation");
            response.setStatusCode(400);
        }

        return response;
    }



    public Response getUserById(Long userId){
        Response response = new Response();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            User requestingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new OurException("Requesting user not found"));

            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User not found"));

            boolean isAuthorized = requestingUser.getUserId().equals(user.getUserId()) ||
                    organisationRepository.findByUser(requestingUser.getUserId(), requestingUser).stream()
                            .anyMatch(org -> org.getUsers().contains(user));

            if (!isAuthorized) {
                throw new OurException("Not authorized to view this user");
            }

            response.setStatusCode(200);
            response.setStatus("success");
            response.setMessage("User " + user.getUserId());
            response.setData(Map.of(
                    "userId", user.getUserId(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "phone", user.getPhone(),
                    "email", user.getEmail()
            ));
        }catch (OurException e) {
            response.setStatus("Bad Request");
            response.setMessage("Not authorised to get this user record");
            response.setStatusCode(400);
        }

        return response;
    }

    @Override
    public Response addByUserId(Long orgId, String userId) {
        Response response = new Response();

        try{
            Organisation organisation = organisationRepository.findById(orgId)
                    .orElseThrow(() -> new OurException("Organisation not found"));


            User existingUser = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new OurException("User not found"));

            organisation.getUsers().add(existingUser);

            organisationRepository.save(organisation);

            response.setStatusCode(200);
            response.setStatus("success");
            response.setMessage("User added to organisation successfully");

        }catch (OurException e) {
            response.setStatus("Bad Request");
            response.setMessage(e.getMessage());
            response.setStatusCode(400);
        }
        return response;
    }







}