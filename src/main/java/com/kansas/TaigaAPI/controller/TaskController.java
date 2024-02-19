package com.kansas.TaigaAPI.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.ProjectService;
import com.kansas.TaigaAPI.service.TasksService;
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
    public List<JsonNode> getClosedTasks(@RequestParam("project") String project){

        ProjectService projectService =new ProjectService();
        int projectId=projectService.getProjectId(authenticationService.getAuthToken(), project);
        return tasksService.getClosedTasks(projectId, authenticationService.getAuthToken());
    }

    @GetMapping("/closedByDate")
    public HashMap getTasksClosedByDate(@RequestParam("project") int projectId, @RequestParam("sprint") int sprintId){
        return tasksService.getTasksClosedByDate(projectId,sprintId,authenticationService.getAuthToken());
    }

}
