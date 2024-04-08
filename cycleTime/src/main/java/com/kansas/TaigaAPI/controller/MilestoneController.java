package com.kansas.TaigaAPI.controller;

import com.kansas.TaigaAPI.model.*;
import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.ProjectService;
import com.kansas.TaigaAPI.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MilestoneController {

    @Autowired
    private AuthenticationService authenticationService;


    @Autowired
    private TasksService tasksService;

    @Autowired
    private ProjectService projectService;


    // Cycle Time
    @GetMapping("/{projectSlug}/{milestoneId}/getCycleTime")
    public List<CycleTime> getCycleTime(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String projectSlug, @PathVariable int milestoneId) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        int projectId = projectService.getProjectId(authToken, projectSlug);
        return tasksService.getTaskHistory(projectId, milestoneId, authToken);
    }


    @GetMapping("/{projectSlug}/getArbitraryCycleTime")
    public List<ArbitaryCycleTime> getCycleTimeForArbitaryTimeFrame(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String projectSlug, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
//        String authToken = authenticationService.getAuthToken(authorizationHeader);
        int projectId = projectService.getProjectId(authenticationService.getAuthToken(authorizationHeader), projectSlug);
        return tasksService.getCycleTimeForArbitaryTimeFrame(projectId, authenticationService.getAuthToken(authorizationHeader), startDate, endDate);
    }

}
