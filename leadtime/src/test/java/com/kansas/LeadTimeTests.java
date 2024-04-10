package com.kansas;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.kansas.service.ProjectService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.kansas.model.AuthRequest;
import com.kansas.service.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kansas.controller.MilestoneController;

import com.kansas.service.AuthenticationService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.http.HttpHeaders;



@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = LeadTime.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application.properties")
@ExtendWith(MockitoExtension.class)
class LeadTimeTests {
    private MockMvc mockMvc;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private AuthenticationService authenticationService;

    @MockBean
    private MilestoneService milestoneService;

    @InjectMocks
    private MilestoneController milestoneController;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(milestoneController).build();
    }


    @Test
    void login_without_credentials_bad_request() throws Exception{

        mvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_with_bad_credentials_access_denied() throws Exception{
        AuthRequest authRequest = new AuthRequest();
        authRequest.setPassword("test123");
        authRequest.setUsername("johndoe");
        mvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isForbidden());
    }

    public static String loadMockData(String filePath) throws IOException {
        ClassPathResource cpr = new ClassPathResource(filePath);
        byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
        return new String(bdata, StandardCharsets.UTF_8);
    }




}


