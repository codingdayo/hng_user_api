package com.codingdayo.user_api;

import com.codingdayo.user_api.dto.OrganisationDto;
import com.codingdayo.user_api.dto.OrganisationRequest;
import com.codingdayo.user_api.dto.Response;
import com.codingdayo.user_api.model.Organisation;
import com.codingdayo.user_api.model.User;
import com.codingdayo.user_api.repository.OrganisationRepository;
import com.codingdayo.user_api.repository.UserRepository;
import com.codingdayo.user_api.service.impl.OrganisationImpl;
import com.codingdayo.user_api.service.impl.UserServiceImpl;
import com.codingdayo.user_api.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrganisationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private OrganisationImpl organisationImpl;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext); // Set the mocked SecurityContext in the SecurityContextHolder
    }

    @Test
    void testAddNewOrg_Successful() {
        OrganisationRequest organisationRequest = new OrganisationRequest();
        organisationRequest.setOrgId(1L);
        organisationRequest.setName("New Org");
        organisationRequest.setDescription("Description of new org");

        User user = new User();
        user.setEmail("test@example.com");

        Organisation organisation = new Organisation();
        organisation.setOrgId(organisationRequest.getOrgId());
        organisation.setName(organisationRequest.getName());
        organisation.setDescription(organisationRequest.getDescription());
        organisation.setCreator(user);
        organisation.setUsers(List.of(user));


        when(securityContext.getAuthentication()).thenReturn(authentication); // Return the mocked Authentication object
        when(authentication.getName()).thenReturn("test@example.com"); // Return the email from the mocked Authentication object
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(organisationRepository.save(any(Organisation.class))).thenReturn(organisation);


        Response response = organisationImpl.addNewOrg(organisationRequest);

        assertEquals(201, response.getStatusCode());
        assertEquals("success", response.getStatus());
        assertEquals("Organisation created successfully", response.getMessage());
        assertTrue(((Map<?, ?>)response.getData()).containsKey("orgId"));
        assertTrue(((Map<?, ?>)response.getData()).containsKey("firstName"));
        assertTrue(((Map<?, ?>)response.getData()).containsKey("lastName"));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(organisationRepository, times(1)).save(any(Organisation.class));
    }

//    find all orgs
@Test
void testFindAllOrg_Successful() {
    User user = new User();
    user.setEmail("test@example.com");
    user.setUserId(1L);

    Organisation organisation1 = new Organisation();
    organisation1.setOrgId(1L);
    organisation1.setName("Org 1");
    organisation1.setDescription("Description 1");
    organisation1.setCreator(user);

    Organisation organisation2 = new Organisation();
    organisation2.setOrgId(2L);
    organisation2.setName("Org 2");
    organisation2.setDescription("Description 2");
    organisation2.setCreator(user);

    List<Organisation> organisationList = List.of(organisation1, organisation2);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("test@example.com");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(organisationRepository.findByUser(user.getUserId(), user)).thenReturn(organisationList);

    Response response = organisationImpl.findAllOrg();

    assertEquals(200, response.getStatusCode());
    assertEquals("success", response.getStatus());
    assertEquals("All Organisations", response.getMessage());
    assertTrue(((Map<?, ?>) response.getData()).containsKey("organisations"));

    List<OrganisationDto> organisationDTOs = organisationList.stream()
            .map(org -> new OrganisationDto(org.getOrgId(), org.getName(), org.getDescription()))
            .collect(Collectors.toList());
    assertEquals(organisationDTOs, ((Map<?, ?>) response.getData()).get("organisations"));

    verify(userRepository, times(1)).findByEmail("test@example.com");
    verify(organisationRepository, times(1)).findByUser(user.getUserId(), user);
}
}
