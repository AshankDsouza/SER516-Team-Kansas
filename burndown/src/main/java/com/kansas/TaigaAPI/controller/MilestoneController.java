package com.kansas.TaigaAPI.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kansas.TaigaAPI.model.*;
import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.MilestoneService;
import com.kansas.TaigaAPI.service.ProjectService;
import com.kansas.TaigaAPI.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class  MilestoneController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private TasksService tasksService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/{milestoneId}/stats")
    public JsonNode getBurnDownMetrics(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int milestoneId) {
        return milestoneService.getBurnDownMetrics(authenticationService.getAuthToken(authorizationHeader),
                milestoneId);
    }

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
        // System.out.println("Value " +value+" Sprint Length "+ jsonNode);
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

    // Burndown chart
    @GetMapping("/{milestoneId}/getBurnDownChart")
    public JsonNode getTotalStoryPoints(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int milestoneId) {
        JsonNode milestoneData = milestoneService
                .getMilestoneData(authenticationService.getAuthToken(authorizationHeader), milestoneId);
        int storyPointsPending = milestoneData.get("total_points").asInt();

        Map<String, Integer> storyPointsCompleted = new HashMap<>();
        for (JsonNode userStory : milestoneData.get("user_stories")) {
            String key;
            if (userStory.has("finish_date") && !userStory.get("finish_date").isNull()) {
                key = userStory.get("finish_date").toString().substring(0, 11) + '"';
            } else {
                key = "null";
            }
            int value = userStory.get("total_points").asInt();
            if (storyPointsCompleted.containsKey(key) && key != "null") {
                int existingSum = storyPointsCompleted.get(key);
                storyPointsCompleted.put(key, existingSum + value);
            } else {
                storyPointsCompleted.put(key, value);
            }
        }
        System.out.println(storyPointsCompleted);
        // add a new field to the arrayNode to contain story points completed per day

        JsonNode rootNode = milestoneService.getBurnDownMetrics(authenticationService.getAuthToken(authorizationHeader),
                milestoneId);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        if (rootNode.isArray()) {
            for (JsonNode elementNode : rootNode) {
                ObjectNode filteredObject = mapper.createObjectNode();
                filteredObject.put("day", elementNode.get("day").asText());
                filteredObject.put("open_points", elementNode.get("open_points").asDouble());
                filteredObject.put("optimal_points", elementNode.get("optimal_points").asDouble());
                if(storyPointsCompleted.containsKey(elementNode.get("day").toString())){
                    storyPointsPending -= storyPointsCompleted.get(elementNode.get("day").toString());
                }
                filteredObject.put("story_points", storyPointsPending);
                arrayNode.add(filteredObject);
            }
        }

        return arrayNode;
    }

    // Cycle Time
    @GetMapping("/{projectSlug}/{milestoneId}/getCycleTime")
    public List<CycleTime> getCycleTime(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String projectSlug, @PathVariable int milestoneId) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        int projectId = projectService.getProjectId(authToken, projectSlug);
        return tasksService.getTaskHistory(projectId, milestoneId, authToken);
    }

    @GetMapping("getDataForLeadTime")
    public ArrayList getDataForLeadTime(@RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("projectSlug") String projectSlug, @RequestParam("sprintId") int sprintId)
            throws ParseException {
        int projectId = projectService.getProjectId(authenticationService.getAuthToken(authorizationHeader),
                projectSlug);

        ArrayList map = new ArrayList();
        JsonNode jsonNode = getMilestoneList(authorizationHeader, projectId);
        // Getting data from taiga api
        JsonNode relData = getGivenSprintData(sprintId, jsonNode);
        relData = relData.get("user_stories");
        for (int i = 0; i < relData.size(); i++) {
            int leadTime = 0;
            int closedTasks = 0;
            if (!relData.get(i).get("finish_date").isNull()) {

                LocalDate createdDate = LocalDate.parse(relData.get(i).get("created_date").toString().substring(1, 11));
                LocalDate finishDate = LocalDate.parse(relData.get(i).get("finish_date").toString().substring(1, 11));
                String userStoryName = (relData.get(i).get("subject")).asText();
                // leadTime += Duration.between(createdDate.toLocalDate().atStartOfDay(),
                // finishDate.toLocalDate().atStartOfDay()).toDays();
                HashMap hs = new HashMap();
                hs.put("created_date", createdDate);
                hs.put("finish_date", finishDate);
                hs.put("userStory_Name", userStoryName);
                // hs.put("lead_time",leadTime);
                map.add(hs);
            }
        }
        return map;
    }

    public JsonNode getGivenSprintData(int sprintId, JsonNode allSprintData) {
        for (int i = 0; i < allSprintData.size(); i++) {
            if (Integer.parseInt(allSprintData.get(i).get("id").toString()) == sprintId) {
                return allSprintData.get(i);
            }
        }
        return null;
    }

    @GetMapping("/{projectSlug}/getTotalPoints")
    public List<TotalPoints> getMilestoneCompletedPoints(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String projectSlug) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        int projectId = projectService.getProjectId(authToken, projectSlug);
        return milestoneService.getMilestoneTotalPoints(authToken, projectId);

    }

    // Work Capacity
    @GetMapping("/{projectSlug}/getCompletedPoints")
    public List<CompletedPoints> getMilestoneTotalCompletedPoints(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable String projectSlug) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        int projectId = projectService.getProjectId(authToken, projectSlug);

        return milestoneService.getMilestoneCompletedPoints(authToken, projectId);

    }


    @GetMapping("/{projectSlug}/multiSprintBundown")
    public HashMap<String,ArrayNode> getmultiSprintBundown(@RequestHeader("Authorization") String authToken,@PathVariable String projectSlug){
        HashMap projectDetails = projectService.getprojectIdAndSprintId(authToken,projectSlug);
        int projectId  = Integer.parseInt(projectDetails.keySet().iterator().next().toString());

        return milestoneService.getMultiSprintBurndown(authToken,projectId, (JsonNode) projectDetails.get(projectId));

    }

    @GetMapping("/getLeadTimeForAbitraryTimeframe")

    public ArrayList getLeadTimeForAbitraryTimeframe(@RequestHeader("Authorization") String authorizationHeader,@RequestParam("projectSlug") String projectSlug, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) throws ParseException {
        int projectId=projectService.getProjectId(authenticationService.getAuthToken(authorizationHeader), projectSlug);
       

        ArrayList map = new ArrayList();
        JsonNode jsonNode = getMilestoneList(authorizationHeader, projectId);
        LocalDate createDateFromString = LocalDate.parse(startDate);
        LocalDate endDateFromString = LocalDate.parse(endDate);

        for (int i = 0; i < jsonNode.size(); i++) {
            int userStoriesCount = jsonNode.get(i).get("user_stories").size();

            for (int j = 0; j < userStoriesCount; j++) {
                JsonNode relData = jsonNode.get(i).get("user_stories");

                LocalDate createdDate = LocalDate.parse(relData.get(j).get("created_date").toString().substring(1, 11));
                if (relData.get(j).get("finish_date") != null) {
                    LocalDate finishDate = LocalDate
                            .parse(relData.get(j).get("finish_date").toString().substring(1, 11));
                    if (!finishDate.isAfter(endDateFromString) && !createdDate.isBefore(createDateFromString)) {

                    System.out.println("Fetching details");
                        HashMap hs=new HashMap();
                        hs.put("created_date", createdDate);
                        hs.put("finish_date", finishDate);
                        hs.put("userStory_Name", (relData.get(j).get("subject")).asText());
                        hs.put("id",(relData.get(j).get("id")).asText());

                        map.add(hs);
                    }
                }
            }
        }
        return map;

    }

    @GetMapping("/{milestoneId}/getEstimateEffectiveness")
    public List<EffectiveEstimatePoints> getEstimateEffectiveness(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable int milestoneId) {
        return tasksService.calculateEstimateEffectiveness(milestoneId,
                authenticationService.getAuthToken(authorizationHeader));

    }

    @GetMapping("/{projectSlug}/getArbitraryCycleTime")
    public List<ArbitaryCycleTime> getCycleTimeForArbitaryTimeFrame(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String projectSlug, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
//        String authToken = authenticationService.getAuthToken(authorizationHeader);
        int projectId = projectService.getProjectId(authenticationService.getAuthToken(authorizationHeader), projectSlug);
        return tasksService.getCycleTimeForArbitaryTimeFrame(projectId, authenticationService.getAuthToken(authorizationHeader), startDate, endDate);
    }

}
