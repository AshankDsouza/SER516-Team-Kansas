package com.kansas.TaigaAPI;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;import com.kansas.TaigaAPI.model.AuthRequest;
import com.kansas.TaigaAPI.model.TotalPoints;
import com.kansas.TaigaAPI.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kansas.TaigaAPI.controller.MilestoneController;
import com.kansas.TaigaAPI.model.CycleTime;
import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.TasksService;
import com.kansas.TaigaAPI.service.MilestoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.http.MediaType.APPLICATION_JSON;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.kansas.TaigaAPI.service.AuthenticationService;


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

	@Mock
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

		given(authenticationService.getAuthToken()).willReturn(authToken);
		given(projectService.getProjectId(authToken, projectSlug)).willReturn(projectId);
		given(tasksService.getTaskHistory(projectId, milestoneId, authToken)).willReturn(expectedCycleTimes);

		String expectedJson = objectMapper.writeValueAsString(expectedCycleTimes);

		mockMvc.perform(get("/api/" + projectSlug + "/" + milestoneId + "/getCycleTime")
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

		given(authenticationService.getAuthToken()).willReturn(authToken);
		given(projectService.getProjectId(authToken, projectSlug)).willReturn(projectId);
//		given(milestoneService.getMilestoneList(authToken,projectId)).willReturn(sprintData);
		given(milestoneService.getMilestoneTotalPoints(authToken,projectId)).willReturn(totalPointsList);

		String expectedJson = objectMapper.writeValueAsString(totalPointsList);

		mockMvc.perform(get("/api/" + projectSlug + "/getTotalPoints")
						.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}




}
