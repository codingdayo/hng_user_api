package com.codingdayo.user_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.codingdayo.user_api.dto.LoginRequest;
import com.codingdayo.user_api.dto.OrganisationRequest;
import com.codingdayo.user_api.dto.RegisterRequest;
import com.codingdayo.user_api.dto.Response;
import com.codingdayo.user_api.model.Organisation;
import com.codingdayo.user_api.model.User;
import com.codingdayo.user_api.repository.OrganisationRepository;
import com.codingdayo.user_api.repository.UserRepository;
import com.codingdayo.user_api.service.UserService;
import com.codingdayo.user_api.service.impl.OrganisationImpl;
import com.codingdayo.user_api.service.impl.UserServiceImpl;
import com.codingdayo.user_api.utils.JWTUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testRegister_SuccessfulRegistration() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserId(1L);
        registerRequest.setEmail("test@example.com");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setPhone("1234567890");
        registerRequest.setPassword("password");

        User user = new User();
        user.setUserId(registerRequest.getUserId());
        user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhone(registerRequest.getPhone());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setOrganisations(new ArrayList<>());


        Organisation organisation = new Organisation();
        organisation.setName(registerRequest.getFirstName() + "'s Organisation");
        organisation.setDescription("...");
        organisation.setCreator(user);
        organisation.setUsers(new ArrayList<>(List.of(user)));

        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtils.generateToken(any(User.class))).thenReturn("token");

        Response response = userService.register(registerRequest);

        assertEquals(201, response.getStatusCode());
        assertEquals("success", response.getStatus());
        assertEquals("Registration Successful", response.getMessage());
        Map<String, Object> data = (Map<String, Object>) response.getData();


        verify(userRepository, times(1)).save(any(User.class));
        verify(organisationRepository, times(1)).save(any(Organisation.class));
        verify(jwtUtils, times(1)).generateToken(any(User.class));

        assertNotNull(data.get("accessToken"));
        assertNotNull(data.get("user"));

    }

    //@Test
    //void testRegister_EmailAlreadyExists() {
    //    RegisterRequest registerRequest = new RegisterRequest();
    //    registerRequest.setEmail("test@example.com");
    //
    //    when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
    //
    //    Response response = userService.register(registerRequest);
    //
    //    assertEquals(400, response.getStatusCode());
    //    assertEquals("Bad Request", response.getStatus());
    //    assertEquals("Registration unsuccessful", response.getMessage());
    //    verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());
    //}

    @Test
    void testLogin_Successful() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        User user = new User();
        user.setUserId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@example.com");
        user.setPhone("1234567890");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(user)).thenReturn("mockedToken");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        Response response = userService.login(loginRequest);

        assertEquals(200, response.getStatusCode());
        assertEquals("success", response.getStatus());
        assertEquals("Login successful", response.getMessage());
        //assertTrue(((Map<?, ?>) response.getData()).containsKey("accessToken"));
        //assertTrue(((Map<?, ?>) response.getData()).containsKey("user"));


        Map<String, Object> data = (Map<String, Object>) response.getData();
        assertTrue(data.containsKey("accessToken"));
        assertEquals("mockedToken", data.get("accessToken"));

        Map<String, String> userData = (Map<String, String>) data.get("user");
        assertEquals("1", userData.get("userId"));
        assertEquals("John", userData.get("firstName"));
        assertEquals("Doe", userData.get("lastName"));
        assertEquals("test@example.com", userData.get("email"));
        assertEquals("1234567890", userData.get("phone"));


        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtUtils, times(1)).generateToken(user);
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        Response response = userService.login(loginRequest);

        assertEquals(401, response.getStatusCode());
        assertEquals("Bad Request", response.getStatus());
        assertEquals("Authentication failed", response.getMessage());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtUtils, times(0)).generateToken(any(User.class));
    }

    //
    //
    //@Test
    //void testRegister_MissingRequiredFields() {
    //    RegisterRequest registerRequest = new RegisterRequest();
    //    // Missing required fields
    //    registerRequest.setEmail("test@example.com");
    //    registerRequest.setPassword("password");
    //
    //
    //    Response response = userService.register(registerRequest);
    //
    //
    //    assertEquals(422, response.getStatusCode()); // Fixed: Ensure correct status code
    //    assertEquals("Bad Request", response.getStatus());
    //
    //    Map<String, Object> responseData = (Map<String, Object>) response.getData();
    //    List<Map<String, String>> errors = (List<Map<String, String>>) responseData.get("errors");
    //
    //    assertTrue(errors.stream().anyMatch(error -> "firstName".equals(error.get("field")) && "Name is required".equals(error.get("message"))));
    //    assertTrue(errors.stream().anyMatch(error -> "lastName".equals(error.get("field")) && "Name is required".equals(error.get("message"))));
    //
    //    verify(userRepository, times(0)).save(any(User.class));
    //    verify(organisationRepository, times(0)).save(any(Organisation.class));
    //    verify(jwtUtils, times(0)).generateToken(any(User.class));
    //}




}
