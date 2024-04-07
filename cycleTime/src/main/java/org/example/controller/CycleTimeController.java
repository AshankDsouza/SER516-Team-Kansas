package org.example.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CycleTimeController {

    @Autowired
    //private CycleTimeService cycleTimeService;
    @GetMapping("/{projectSlug}/{milestoneId}/getCycleTime")
    public String getCycleTime(@RequestHeader("Authorization") String authorizationHeader,
                               @PathVariable String projectSlug, @PathVariable int milestoneId) {
        // String authToken = authenticationService.getAuthToken(authorizationHeader);
        //   int projectId = cycleTimeService.getProjectId(authToken, projectSlug);
        //return cycleTimeService.getTaskHistory(projectId, milestoneId, authToken);
        return null;
    }
}
