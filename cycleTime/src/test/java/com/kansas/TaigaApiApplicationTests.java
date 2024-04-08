package com.kansas;


import com.kansas.model.ArbitaryCycleTime;
import com.kansas.model.CycleTime;

import org.mockito.Mockito;
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
import com.kansas.service.TasksService;

import org.junit.jupiter.api.BeforeEach;
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
		classes = CycleTimeApplication.class)
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



	public static String loadMockData(String filePath) throws IOException {
		ClassPathResource cpr = new ClassPathResource(filePath);
		byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
		return new String(bdata, StandardCharsets.UTF_8);
	}

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

