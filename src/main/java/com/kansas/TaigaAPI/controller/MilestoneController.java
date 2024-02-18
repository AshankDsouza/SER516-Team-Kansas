package com.kansas.TaigaAPI.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kansas.TaigaAPI.model.CycleTime;
import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.MilestoneService;
import com.kansas.TaigaAPI.service.ProjectService;
import com.kansas.TaigaAPI.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    private TasksService tasksService;

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
    public HashMap<String,Integer> getAllSprints(@RequestParam("project") String projectSlug){
        ProjectService projectService =new ProjectService();
        int projectId=projectService.getProjectId(authenticationService.getAuthToken(), projectSlug);
        JsonNode jsonNode=getMilestoneList(projectId);
        HashMap<String,Integer> sprintVal= new HashMap<String,Integer>();
           for(int i=0;i<jsonNode.size();i++) {
               sprintVal.put(jsonNode.get(i).get("name").asText(),jsonNode.get(i).get("id").asInt());
           }
        return sprintVal;
    }

    //Burndown chart
    @GetMapping("/{milestoneId}/getBurnDownChart")
    public JsonNode getTotalStoryPoints(@PathVariable int milestoneId) {
        JsonNode rootNode = milestoneService.getBurnDownMetrics(authenticationService.getAuthToken(), milestoneId);

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        if (rootNode.isArray()) {

            for (JsonNode elementNode : rootNode) {
                ObjectNode filteredObject = mapper.createObjectNode();
                filteredObject.put("day", elementNode.get("day").asText());
                filteredObject.put("open_points", elementNode.get("open_points").asDouble());
                filteredObject.put("optimal_points", elementNode.get("optimal_points").asDouble());

                arrayNode.add(filteredObject);
            }
        }
        return arrayNode;
    }
    //Cycle Time
    @GetMapping("/{projectId}/{milestoneId}/getCycleTime")
    public List<CycleTime> getCycleTime(@PathVariable int projectId, @PathVariable int milestoneId){
        return tasksService.getTaskHistory(projectId,milestoneId,authenticationService.getAuthToken());
    }




}
