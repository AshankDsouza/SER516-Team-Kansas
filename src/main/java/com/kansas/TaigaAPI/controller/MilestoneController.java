package com.kansas.TaigaAPI.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/milestones")
public class MilestoneController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MilestoneService milestoneService;

    @GetMapping("/{milestoneId}/stats")
    public JsonNode getBurnDownMetrics(@PathVariable int milestoneId) {
        return milestoneService.getBurnDownMetrics(authenticationService.getAuthToken(), milestoneId);
    }

    @GetMapping("")
    public JsonNode getMilestoneList(@RequestParam("project") int projectId) {
        return milestoneService.getMilestoneList(authenticationService.getAuthToken(), projectId);
    }


}
