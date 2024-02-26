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

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.kansas.TaigaAPI.service.TasksService.*;



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
    @GetMapping("/{projectSlug}/{milestoneId}/getCycleTime")
    public List<CycleTime> getCycleTime(@PathVariable String projectSlug, @PathVariable int milestoneId){
        ProjectService projectService =new ProjectService();
        int projectId=projectService.getProjectId(authenticationService.getAuthToken(), projectSlug);
        return tasksService.getTaskHistory(projectId,milestoneId,authenticationService.getAuthToken());
    }

    @GetMapping("getDataForLeadTime")
    public ArrayList getDataForLeadTime(@RequestParam("projectSlug") String projectSlug, @RequestParam("sprintId") int sprintId) throws ParseException {
        ProjectService projectService =new ProjectService();
        int projectId=projectService.getProjectId(authenticationService.getAuthToken(), projectSlug);

        ArrayList map = new ArrayList();
        JsonNode jsonNode=getMilestoneList(projectId);
        //Getting data from taiga api
        JsonNode relData = getGivenSprintData(sprintId,jsonNode);
        relData = relData.get("user_stories");
        for(int i=0;i<relData.size();i++)
        {
            int leadTime = 0;
            int closedTasks = 0;
            if(!relData.get(i).get("finish_date").isNull()){

            String createdDate = relData.get(i).get("created_date").toString();
            String finishDate = relData.get(i).get("finish_date").toString();
            String userStoryName =(relData.get(i).get("subject")).asText();
//            leadTime += Duration.between(createdDate.toLocalDate().atStartOfDay(), finishDate.toLocalDate().atStartOfDay()).toDays();
            HashMap hs=new HashMap();
            hs.put("created_date",createdDate);
            hs.put("finish_date", finishDate);
            hs.put("userStory_Name", userStoryName);
//            hs.put("lead_time",leadTime);
                map.add(hs);
        }}
        return map;
    }

    public JsonNode getGivenSprintData(int sprintId, JsonNode allSprintData){
        for(int i=0;i<allSprintData.size();i++){
            if (Integer.parseInt(allSprintData.get(i).get("id").toString())==sprintId){
                return allSprintData.get(i);
            }
        }
        return null;
    }

}
