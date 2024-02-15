package com.kansas.TaigaAPI.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @GetMapping("getDataForLeadTime")
    public HashMap<Integer, HashMap<String, Date>> getDataForLeadTime(@RequestParam("projectId") int projectId, @RequestParam("sprintNo") int sprintNo) throws ParseException {
        HashMap<Integer, HashMap<String, Date>> map = new HashMap<>();
        JsonNode jsonNode=getMilestoneList(projectId);
        int jsonIndexForGivenSprint=jsonNode.size()-sprintNo;
        int userStoryCount= jsonNode.get(jsonIndexForGivenSprint).get("user_stories").size();
       // System.out.println("Sprint Index"+ jsonIndexForGivenSprint+" userStorySize "+userStoryCount);
        for(int i=0;i<userStoryCount;i++)
        {

            if(jsonNode.get(jsonIndexForGivenSprint).get("user_stories").get(i).get("created_date")!=null){
            String createdDateStr = (jsonNode.get(jsonIndexForGivenSprint).get("user_stories").get(i).get("created_date")).asText();
            String finishDateStr = (jsonNode.get(jsonIndexForGivenSprint).get("user_stories").get(i).get("finish_date")).asText();
            // Parse string to Date object
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date createdDate = dateFormat.parse(createdDateStr);
            Date finishDate=null;
            if(finishDateStr.isEmpty()) {
                 finishDate = dateFormat.parse(finishDateStr);
            }
                HashMap<String,Date> hs=new HashMap<String,Date>();
            hs.put("created_date",createdDate);
            hs.put("finish_date", finishDate);
           // System.out.println("Date "+ i +" "+ createdDate);
                map.put(i+1,hs);
        }}
        return map;
    }



}
