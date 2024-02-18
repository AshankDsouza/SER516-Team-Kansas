package com.kansas.TaigaAPI.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kansas.TaigaAPI.model.CycleTime;
import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.MilestoneService;
import com.kansas.TaigaAPI.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
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
    public HashMap<String,Integer> getAllSprints(@RequestParam("project") int projectId){
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

    @GetMapping("getDataForLeadTime")
    public HashMap<Integer, HashMap<String, String>> getDataForLeadTime(@RequestParam("projectId") int projectId, @RequestParam("sprintNo") int sprintNo) throws ParseException {
        HashMap<Integer, HashMap<String, String>> map = new HashMap<>();
        JsonNode jsonNode=getMilestoneList(projectId);


        int jsonIndexForGivenSprint=jsonNode.size()-sprintNo;
        int userStoryCount= jsonNode.get(jsonIndexForGivenSprint).get("user_stories").size();
       // System.out.println("Sprint Index"+ jsonIndexForGivenSprint+" userStorySize "+userStoryCount);
        for(int i=0;i<userStoryCount;i++)
        {

            if(jsonNode.get(jsonIndexForGivenSprint).get("user_stories").get(i).get("created_date")!=null){
            String createdDateStr = (jsonNode.get(jsonIndexForGivenSprint).get("user_stories").get(i).get("created_date")).asText();
            String finishDateStr = (jsonNode.get(jsonIndexForGivenSprint).get("user_stories").get(i).get("finish_date")).asText();
            String userStoryName =(jsonNode.get(jsonIndexForGivenSprint).get("user_stories").get(i).get("subject")).asText();
                HashMap<String,String> hs=new HashMap<String,String>();
            hs.put("created_date",createdDateStr);
            hs.put("finish_date", finishDateStr);
            hs.put("userStory_Name", userStoryName);
           // System.out.println("Date "+ i +" "+ createdDate);
                map.put(i+1,hs);
        }}
        return map;
    }



}
