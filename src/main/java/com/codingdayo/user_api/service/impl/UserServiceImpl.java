package com.codingdayo.user_api.service.impl;

import com.codingdayo.user_api.dto.LoginRequest;
import com.codingdayo.user_api.dto.RegisterRequest;
import com.codingdayo.user_api.dto.Response;
import com.codingdayo.user_api.exceptions.EmailAlreadyExistsException;
import com.codingdayo.user_api.exceptions.OurException;
import com.codingdayo.user_api.model.Organisation;
import com.codingdayo.user_api.model.User;
import com.codingdayo.user_api.repository.OrganisationRepository;
import com.codingdayo.user_api.repository.UserRepository;
import com.codingdayo.user_api.service.UserService;
import com.codingdayo.user_api.utils.JWTUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Response register(RegisterRequest registerRequest) {
       Response response = new Response();
        try {

            if (userRepository.existsByEmail(registerRequest.getEmail())){
                throw new EmailAlreadyExistsException("Email already exists");
            }

            User user = new User();

            user.setEmail(registerRequest.getEmail());
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setPhone(registerRequest.getPhone());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setOrganisations(new ArrayList<>());

            User savedUser = userRepository.save(user);

            Organisation organisation = new Organisation();
            organisation.setName(savedUser.getFirstName() + "'s Organisation");
            organisation.setDescription("...");
            organisation.setCreator(user);
            organisation.setUsers(new ArrayList<>(List.of(user)));

            organisationRepository.save(organisation);

            //var jwt = jwtUtils.generateToken(savedUser);

            String token = jwtUtils.generateToken(savedUser);

            savedUser.setOrganisations(new ArrayList<>(List.of(organisation)));
            response.setStatusCode(201);
            response.setStatus("success");
            response.setMessage("Registration Successful");
            response.setData(Map.of(
                    "accessToken", token,
                    "user", Map.of(
                            "userId", savedUser.getUserId().toString(),
                            "firstName", savedUser.getFirstName(),
                            "lastName", savedUser.getLastName(),
                            "email", savedUser.getEmail(),
                      "phone", savedUser.getPhone()
                    )));


     }catch (EmailAlreadyExistsException e) {
            throw e;
        }
            catch (OurException e) {
            response.setStatus("Bad Request");
            response.setMessage("Registration unsuccessful");
            response.setStatusCode(400);
        }


        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new OurException("user Not found"));

            String token = jwtUtils.generateToken(user);

            response.setStatusCode(200);
            response.setStatus("success");
            response.setMessage("Login successful");
            response.setData(Map.of(
                    "accessToken", token,
                    "user", Map.of(
                            "userId", user.getUserId().toString(),
                            "firstName", user.getFirstName(),
                            "lastName", user.getLastName(),
                            "email", user.getEmail(),
                            "phone", user.getPhone()
                    )));

        } catch (BadCredentialsException e) {
            throw e;
        }
        return response;
    }


    //public String generateToken(Long userId) {
    //    User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User not found"));
    //    return jwtUtils.generateToken(user);
    //}




}
