package com.kansas;

import com.fasterxml.jackson.databind.JsonNode;
import com.kansas.model.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.kansas.service.ProjectService;

import com.kansas.model.AuthRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kansas.controller.MilestoneController;

import com.kansas.service.AuthenticationService;
import com.kansas.service.MilestoneService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;


import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.http.MediaType.APPLICATION_JSON;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.http.HttpHeaders;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Velocity.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application.properties")
@ExtendWith(MockitoExtension.class)
public class VelocityTests {
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

    @Test
    public void getTotalPointsSuccess() throws Exception {
        String projectSlug = "ser516asu-ser516-team-kansas";
        int projectId = 1;
        String authToken = "auth-token";

        List<TotalPoints> totalPointsList = new ArrayList<>(); ;
        JsonNode jsonData =  objectMapper.readTree(loadMockData("totalPointsMock.json"));
        JsonNode sprintData =  objectMapper.readTree(loadMockData("sprintList.json"));
        totalPointsList.add(new TotalPoints("Sprint 5",0));
        totalPointsList.add(new TotalPoints("Sprint 4",0));
        totalPointsList.add(new TotalPoints("Sprint 3",0));
        totalPointsList.add(new TotalPoints("Sprint 2",37));
        totalPointsList.add(new TotalPoints("Sprint 1",41));

        given(authenticationService.getAuthToken(authToken)).willReturn(authToken);
        given(projectService.getProjectId(authToken, projectSlug)).willReturn(projectId);
//		given(milestoneService.getMilestoneList(authToken,projectId)).willReturn(sprintData);
        given(milestoneService.getMilestoneTotalPoints(authToken,projectId)).willReturn(totalPointsList);

        String expectedJson = objectMapper.writeValueAsString(totalPointsList);

        mockMvc.perform(get("/api/" + projectSlug + "/getTotalPoints")
                        .header(HttpHeaders.AUTHORIZATION,   authToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }


}



