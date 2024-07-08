package com.codingdayo.user_api.service;

import com.codingdayo.user_api.dto.LoginRequest;
import com.codingdayo.user_api.dto.RegisterRequest;
import com.codingdayo.user_api.dto.Response;
import com.codingdayo.user_api.model.User;

public interface UserService {

    Response register(RegisterRequest registerRequest);

    Response login (LoginRequest loginRequest);

   //String generateToken(Long userId);

}
