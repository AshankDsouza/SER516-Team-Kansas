package com.kansas.controller;

import com.fasterxml.jackson.databind.JsonNode;

import com.kansas.model.*;
import com.kansas.service.AuthenticationService;
import com.kansas.service.MilestoneService;
import com.kansas.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.kansas.model.EffectivenessEstimatePoints;
import com.kansas.service.TasksService;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MilestoneController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MilestoneService milestoneService;


    @Autowired
    private ProjectService projectService;

    @Autowired
    private TasksService tasksService;


    @GetMapping("/getMilestoneList")
    public JsonNode getMilestoneList(@RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("project") int projectId) {
        return milestoneService.getMilestoneList(authenticationService.getAuthToken(authorizationHeader), projectId);
    }

    @GetMapping("/getMilestoneListForSprint")
    public JsonNode getMilestoneListForSprint(@RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("project") int projectId, @RequestParam("sprintNo") int sprintNo) {
        JsonNode jsonNode = getMilestoneList(authorizationHeader, projectId);
        int value = jsonNode.size() - sprintNo;
        return jsonNode.get(value);
    }

    @GetMapping("/getAllSprints")
    public HashMap<String, Integer> getAllSprints(@RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("project") String projectSlug) {
        ProjectService projectService = new ProjectService();
        int projectId = projectService.getProjectId(authenticationService.getAuthToken(authorizationHeader),
                projectSlug);
        JsonNode jsonNode = getMilestoneList(authorizationHeader, projectId);
        HashMap<String, Integer> sprintVal = new HashMap<String, Integer>();
        for (int i = 0; i < jsonNode.size(); i++) {
            sprintVal.put(jsonNode.get(i).get("name").asText(), jsonNode.get(i).get("id").asInt());
        }
        return sprintVal;
    }



    @GetMapping("/{projectSlug}/getTotalPoints")
    public List<TotalPoints> getMilestoneCompletedPoints(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String projectSlug) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        int projectId = projectService.getProjectId(authToken, projectSlug);
        return milestoneService.getMilestoneTotalPoints(authToken, projectId);
    }

    @GetMapping("/{milestoneId}/getEstimateEffectiveness")
    public List<EffectivenessEstimatePoints> getEstimateEffectiveness(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable int milestoneId) {
                System.out.println(milestoneId);
        return tasksService.calculateEstimateEffectiveness(milestoneId, authenticationService.getAuthToken(authorizationHeader));
    }


}
