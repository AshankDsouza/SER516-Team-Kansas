package com.kansas.TaigaAPI.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;

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

    @GetMapping("/getMilestoneList")
    public JsonNode getMilestoneList(@RequestParam("project") int projectId) {
        return milestoneService.getMilestoneList(authenticationService.getAuthToken(), projectId);
    }

    @GetMapping("/getMilestoneListForSprint")
    public JsonNode getMilestoneListForSprint(@RequestParam("project") int projectId, @RequestParam("sprintNo") int sprintNo){
        JsonNode jsonNode=getMilestoneList(projectId);
        int value= jsonNode.size()-sprintNo;
      //  System.out.println("Value " +value+" Sprint Length "+ jsonNode);
        return jsonNode.get(value);
    }

    @GetMapping("/getAllSprints")
    public HashMap<String,Integer> getAllSprints(@RequestParam("project") int projectId){
        JsonNode jsonNode=getMilestoneList(projectId);
        HashMap<String,Integer> sprintVal= new HashMap<String,Integer>();
           for(int i=0;i<jsonNode.size();i++) {
           // System.out.println(jsonNode.get(i).get("id"));
               sprintVal.put(jsonNode.get(i).get("name").asText(),jsonNode.get(i).get("id").asInt());
           }
        return sprintVal;

    }

}
