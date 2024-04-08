//package com.kansas;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.kansas.model.*;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.util.FileCopyUtils;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//import com.kansas.service.ProjectService;
//
//import com.kansas.model.AuthRequest;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kansas.controller.MilestoneController;
//
//import com.kansas.service.AuthenticationService;
//import com.kansas.service.MilestoneService;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.BDDMockito.given;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import org.springframework.http.MediaType;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.http.HttpHeaders;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.beans.factory.annotation.Autowired;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.kansas.service.*;
//import com.kansas.model.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
//        classes = EstimateEffectivenessTests.class)
//@AutoConfigureMockMvc
//@TestPropertySource(
//        locations = "classpath:application.properties")
//@ExtendWith(MockitoExtension.class)
//public class EstimateEffectivenessTests {
//    private MockMvc mockMvc;
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Mock
//    private ProjectService projectService;
//
//    @Mock
//    private TasksService tasksService;
//
//    @Mock
//    private AuthenticationService authenticationService;
//
//    @MockBean
//    private MilestoneService milestoneService;
//
//    @InjectMocks
//    private MilestoneController milestoneController;
//
//    private  ObjectMapper objectMapper;
//
//
//    @BeforeEach
//    void setUp() {
//        objectMapper = new ObjectMapper();
//        mockMvc = MockMvcBuilders.standaloneSetup(milestoneController).build();
//    }
//
//
//    public static String loadMockData(String filePath) throws IOException {
//        ClassPathResource cpr = new ClassPathResource(filePath);
//        byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
//        return new String(bdata, StandardCharsets.UTF_8);
//    }
//
//    @Test
//    void calculateEstimateEffectiveness_Success() throws JsonProcessingException {
//        int milestoneId = 100;
//        String authToken = "authToken";
//
//        String milestoneDataJson = """
//                {
//                  "user_stories": [
//                    {
//                      "is_closed": true,
//                      "total_points": 5,
//                      "subject": "Closed Story",
//                      "created_date": "2023-01-01T00:00:00Z",
//                      "finish_date": "2023-01-03T00:00:00Z"
//                    },
//                    {
//                      "is_closed": false,
//                      "total_points": 3,
//                      "subject": "Open Story"
//                    }
//                  ]
//                }
//                """;
//
//        JsonNode milestoneData = objectMapper.readTree(milestoneDataJson);
//
//        when(milestoneService.getMilestoneData(authToken, milestoneId)).thenReturn(milestoneData);
//
//        List<EffectivenessEstimatePoints> result = tasksService.calculateEstimateEffectiveness(milestoneId, authToken);
//
//        assertNotNull(result);
//        assertEquals(0, result.size());
//    }
//
//
//}
//
//
//
