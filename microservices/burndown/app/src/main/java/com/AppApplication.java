package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.http.ResponseEntity;


import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.kansas.TaigaAPI.model.*;
//import com.kansas.TaigaAPI.service.AuthenticationService;
//import com.kansas.TaigaAPI.service.MilestoneService;
//import com.kansas.TaigaAPI.service.ProjectService;
//import com.kansas.TaigaAPI.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//import java.text.ParseException;
//import java.time.LocalDate;
//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

import com.service.AuthenticationService;
import com.service.MilestoneService;
import com.service.ProjectService;
import com.service.TasksService;


@SpringBootApplication
@RestController // Add this annotation
public class AppApplication {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private TasksService tasksService;

    @Autowired
    private ProjectService projectService;

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

    @GetMapping("/{projectSlug}/multiSprintBundown")
    public HashMap<String,ArrayNode> getmultiSprintBundown(@RequestHeader("Authorization") String authorizationHeader,@PathVariable String projectSlug){
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        HashMap projectDetails = projectService.getprojectIdAndSprintId(authToken,projectSlug);
        int projectId  = Integer.parseInt(projectDetails.keySet().iterator().next().toString());

        return milestoneService.getMultiSprintBurndown(authToken,projectId, (JsonNode) projectDetails.get(projectId));

    }
	




}
