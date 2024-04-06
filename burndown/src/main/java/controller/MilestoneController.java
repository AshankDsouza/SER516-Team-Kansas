package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.kansas.TaigaAPI.model.*;
import service.AuthenticationService;
import service.MilestoneService;
import service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("")
public class MilestoneController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private ProjectService projectService;


    @GetMapping("/{projectSlug}/multiSprintBundown")
    public HashMap<String,ArrayNode> getmultiSprintBundown(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String projectSlug){
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        HashMap projectDetails = projectService.getprojectIdAndSprintId(authToken,projectSlug);
        int projectId  = Integer.parseInt(projectDetails.keySet().iterator().next().toString());

        return milestoneService.getMultiSprintBurndown(authToken,projectId, (JsonNode) projectDetails.get(projectId));
    }


}
