package com.codingdayo.user_api.controller;


import com.codingdayo.user_api.dto.AddUserById;
import com.codingdayo.user_api.dto.OrganisationRequest;
import com.codingdayo.user_api.dto.Response;
import com.codingdayo.user_api.service.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrganisationController {

    @Autowired
    private OrganisationService organisationService;

    @PostMapping("/organisations")
    public ResponseEntity<Response> add(@RequestBody OrganisationRequest organisationRequest){
            Response response = organisationService.addNewOrg(organisationRequest);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

    @GetMapping("/organisations")
    public ResponseEntity<Response> findAll(){
        Response response = organisationService.findAllOrg();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/organisations/{orgId}")
    public ResponseEntity<Response> findOrgById(@PathVariable("orgId") Long orgId){
        Response response = organisationService.findByOrgId(orgId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Response> getUserById(@PathVariable("userId") Long userId){
        Response response = organisationService.getUserById(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/organisations/{orgId}/users")
    public ResponseEntity<Response> addUserByIdToOrg(@PathVariable("orgId") Long orgId, @RequestBody AddUserById addById){
        Response response = organisationService.addByUserId(orgId, String.valueOf(addById.getUserId()));
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


}
