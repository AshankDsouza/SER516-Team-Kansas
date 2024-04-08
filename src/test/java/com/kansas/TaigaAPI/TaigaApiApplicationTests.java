package com.kansas.TaigaAPI;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kansas.TaigaAPI.model.*;
import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.kansas.TaigaAPI.service.ProjectService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.kansas.TaigaAPI.model.AuthRequest;
import com.kansas.TaigaAPI.service.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kansas.TaigaAPI.controller.MilestoneController;

import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.TasksService;
import com.kansas.TaigaAPI.service.MilestoneService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.http.MediaType.APPLICATION_JSON;


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
		classes = TaigaApiApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
		locations = "classpath:application.properties")
@ExtendWith(MockitoExtension.class)
class TaigaApiApplicationTests {
	private MockMvc mockMvc;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	private ProjectService projectService;

	@Mock
	private TasksService tasksService;

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
	public void getCycleTime_ShouldReturnCycleTimes() throws Exception {
		String projectSlug = "ser516asu-ser516-team-kansas";
		int milestoneId = 376621;
		int projectId = 1;
		String authToken = "auth-token";

		List<CycleTime> expectedCycleTimes = new ArrayList<>();

		expectedCycleTimes.add(new CycleTime("ABC",10,2));

		given(authenticationService.getAuthToken(authToken)).willReturn(authToken);
		given(projectService.getProjectId(authToken, projectSlug)).willReturn(projectId);
		given(tasksService.getTaskHistory(projectId, milestoneId, authToken)).willReturn(expectedCycleTimes);

		String expectedJson = objectMapper.writeValueAsString(expectedCycleTimes);

		mockMvc.perform(get("/api/" + projectSlug + "/" + milestoneId + "/getCycleTime")
						.header(HttpHeaders.AUTHORIZATION,   authToken)
						.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
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
	@Test
	public void testGetMilestoneTotalCompletedPoints() throws Exception {
		String projectSlug = "test-project";
		String authToken = "auth-token";
		int  projectId = 123;
		List<CompletedPoints> mockCompletedPoints = new ArrayList<>();

		when(projectService.getProjectId(authToken,projectSlug)).thenReturn(projectId);
		when(authenticationService.getAuthToken(authToken)).thenReturn(authToken);
		when(milestoneService.getMilestoneCompletedPoints(authToken, projectId)).thenReturn(mockCompletedPoints);

		mockMvc.perform(get("/api/{projectSlug}/getCompletedPoints", projectSlug)
						.header(HttpHeaders.AUTHORIZATION,   authToken))
				.andExpect(status().isOk());

		verify(authenticationService, times(1)).getAuthToken(authToken);
		verify(milestoneService, times(1)).getMilestoneCompletedPoints(authToken, projectId);
	}



//	@Test
//	void login_with_valid_credentials_ok() throws Exception {
//		AuthRequest authRequest = new AuthRequest();
//		authRequest.setUsername("johndoe"); // Enter your valid username
//		authRequest.setPassword("test123"); // Enter your valid user password
//		mvc.perform(post("/api/auth")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(objectMapper.writeValueAsString(authRequest)))
//				.andExpect(status().isOk());
//	}
	public static String loadMockData(String filePath) throws IOException {
		ClassPathResource cpr = new ClassPathResource(filePath);
		byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
		return new String(bdata, StandardCharsets.UTF_8);
	}

// 	@Test
// 	public void getTotalPointsSuccess() throws Exception {
// 		String projectSlug = "ser516asu-ser516-team-kansas";
// 		int projectId = 1;
// 		String authToken = "auth-token";

// 		List<TotalPoints> totalPointsList = new ArrayList<>(); ;
// 		JsonNode jsonData =  objectMapper.readTree(loadMockData("totalPointsMock.json"));
// 		JsonNode sprintData =  objectMapper.readTree(loadMockData("sprintList.json"));
// 		totalPointsList.add(new TotalPoints("Sprint 5",0));
// 		totalPointsList.add(new TotalPoints("Sprint 4",0));
// 		totalPointsList.add(new TotalPoints("Sprint 3",0));
// 		totalPointsList.add(new TotalPoints("Sprint 2",37));
// 		totalPointsList.add(new TotalPoints("Sprint 1",41));

// 		given(authenticationService.getAuthToken(authToken)).willReturn(authToken);
// 		given(projectService.getProjectId(authToken, projectSlug)).willReturn(projectId);
// //		given(milestoneService.getMilestoneList(authToken,projectId)).willReturn(sprintData);
// 		given(milestoneService.getMilestoneTotalPoints(authToken,projectId)).willReturn(totalPointsList);

// 		String expectedJson = objectMapper.writeValueAsString(totalPointsList);

// 		mockMvc.perform(get("/api/" + projectSlug + "/getTotalPoints")
// 						.header(HttpHeaders.AUTHORIZATION,   authToken)
// 						.contentType(APPLICATION_JSON))
// 				.andExpect(status().isOk())
// 				.andExpect(content().json(expectedJson));
// 	}

	// @Test
	// void testGetEstimateEffectiveness() {

	// 	String projectSlug = "ser516asu-ser516-team-kansas";
	// 	int milestoneId = 376621;
	// 	String authToken = "auth-token";
	// 	List<EffectiveEstimatePoints> mockResponse = List.of(new EffectiveEstimatePoints(projectSlug, milestoneId));

	// 	when(authenticationService.getAuthToken(authToken)).thenReturn(authToken);
	// 	when(tasksService.calculateEstimateEffectiveness(milestoneId, authToken)).thenReturn(mockResponse);

	// 	List<EffectiveEstimatePoints> result = milestoneController.getEstimateEffectiveness(authToken,milestoneId);

	// 	assertNotNull(result);
	// 	assertEquals(mockResponse, result);
	// 	verify(tasksService).calculateEstimateEffectiveness(milestoneId, authToken);
	// }

	// @Test
	// void calculateEstimateEffectiveness_Success() throws JsonProcessingException {
	// 	int milestoneId = 100;
	// 	String authToken = "authToken";

	// 	String milestoneDataJson = """
    //             {
    //               "user_stories": [
    //                 {
    //                   "is_closed": true,
    //                   "total_points": 5,
    //                   "subject": "Closed Story",
    //                   "created_date": "2023-01-01T00:00:00Z",
    //                   "finish_date": "2023-01-03T00:00:00Z"
    //                 },
    //                 {
    //                   "is_closed": false,
    //                   "total_points": 3,
    //                   "subject": "Open Story"
    //                 }
    //               ]
    //             }
    //             """;

	// 	JsonNode milestoneData = objectMapper.readTree(milestoneDataJson);

	// 	when(milestoneService.getMilestoneData(authToken, milestoneId)).thenReturn(milestoneData);

	// 	List<EffectiveEstimatePoints> result = tasksService.calculateEstimateEffectiveness(milestoneId, authToken);

	// 	assertNotNull(result);
	// 	assertEquals(0, result.size());
	// }

	@Test
	public void getCycleTimeForArbitraryTimeFrame_ShouldReturnList() throws Exception {
		String authToken = "auth-token";
		String projectSlug = "example-project";
		String startDate = "2022-01-01";
		String endDate = "2022-01-31";
		int projectId = 1;

		List<ArbitaryCycleTime> expectedList = Arrays.asList(
				new ArbitaryCycleTime("Task 1", 5),
				new ArbitaryCycleTime("Task 2", 3)
		);

		when(authenticationService.getAuthToken("Bearer " + authToken)).thenReturn(authToken);
		Mockito.when(projectService.getProjectId(authToken, projectSlug)).thenReturn(projectId);
		Mockito.when(tasksService.getCycleTimeForArbitaryTimeFrame(projectId, authToken, startDate, endDate)).thenReturn(expectedList);

		mockMvc.perform(get("/api/{projectSlug}/getArbitraryCycleTime", projectSlug)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
						.param("startDate", startDate)
						.param("endDate", endDate))
				.andExpect(status().isOk());

		Mockito.verify(projectService).getProjectId(authToken, projectSlug);
		Mockito.verify(tasksService).getCycleTimeForArbitaryTimeFrame(projectId, authToken, startDate, endDate);
	}



}

