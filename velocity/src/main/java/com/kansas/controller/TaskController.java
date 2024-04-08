package com.kansas.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kansas.service.AuthenticationService;
import com.kansas.service.ProjectService;
import com.kansas.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")

public class TaskController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TasksService tasksService;

    @GetMapping("")
    public List<JsonNode> getClosedTasks(@RequestHeader("Authorization") String authorizationHeader,@RequestParam("project") String project){

        ProjectService projectService =new ProjectService();
        String auuthToken = authenticationService.getAuthToken(authorizationHeader);
        int projectId=projectService.getProjectId(auuthToken, project);
        return tasksService.getClosedTasks(projectId, auuthToken);
    }



}
