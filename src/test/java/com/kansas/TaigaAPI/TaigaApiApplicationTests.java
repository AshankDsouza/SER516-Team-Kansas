package com.kansas.TaigaAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kansas.TaigaAPI.controller.MilestoneController;
import com.kansas.TaigaAPI.model.CycleTime;
import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.TasksService;
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



@ExtendWith(MockitoExtension.class)
class TaigaApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private TasksService tasksService;

	@Mock
	private AuthenticationService authenticationService;

	@InjectMocks
	private MilestoneController milestoneController;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(milestoneController).build();
	}

	@Test
	public void getCycleTime_ShouldReturnCycleTimes() throws Exception {
		int projectId = 1;
		int milestoneId = 1;
		String authToken = "auth-token";

		List<CycleTime> expectedCycleTimes = new ArrayList<>();

		expectedCycleTimes.add(new CycleTime("ABC",10,2));

		given(authenticationService.getAuthToken()).willReturn(authToken);
		given(tasksService.getTaskHistory(projectId, milestoneId, authToken)).willReturn(expectedCycleTimes);

		String expectedJson = objectMapper.writeValueAsString(expectedCycleTimes);

		mockMvc.perform(get("/api/" + projectId + "/" + milestoneId + "/getCycleTime")
						.contentType(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(expectedJson));
	}

}
