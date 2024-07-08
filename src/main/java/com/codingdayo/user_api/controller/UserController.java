package com.codingdayo.user_api.controller;

import com.codingdayo.user_api.dto.LoginRequest;
import com.codingdayo.user_api.dto.RegisterRequest;
import com.codingdayo.user_api.dto.Response;
import com.codingdayo.user_api.model.User;
import com.codingdayo.user_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> createUser(@RequestBody RegisterRequest registerRequest){
        Response response = userService.register(registerRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest){
        Response response = userService.login(loginRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }



}
