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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.kansas.TaigaAPI.utils.GlobalData;

@RestController
@RequestMapping("/api")
public class MilestoneController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private TasksService tasksService;

    @Autowired
    private ProjectService projectService;

    private static final String VELOCITY_URL = GlobalData.getVelocityURL();
    private static final String BURNDOWN_URL = GlobalData.getBurndownURL();
    private static final String LEADTIME_URL = GlobalData.getLeadTimeURL();
    private static final String CYCLETIME_URL = GlobalData.getCycletimeURL();
    private static final String AUC_URL = GlobalData.getAUC_URL();
    private static final String ESTIMATEEFFECTIVENESS_URL = GlobalData.getEstimateEffectivenessURL();
    private static final String VIP_URL = GlobalData.getVipURL();
    private static final String VALUE_AUC_URL = GlobalData.getValueAucURL();

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
                if (storyPointsCompleted.containsKey(elementNode.get("day").toString())) {
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
    public String getCycleTime(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String projectSlug, @PathVariable int milestoneId) {

        // String authToken = authenticationService.getAuthToken(authorizationHeader);
        // int projectId = projectService.getProjectId(authToken, projectSlug);
        // return tasksService.getTaskHistory(projectId, milestoneId, authToken);

        String authToken = authenticationService.getAuthToken(authorizationHeader);
        String url = CYCLETIME_URL + "/api/" + projectSlug + "/" + milestoneId + "/getCycleTime";
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = responseEntity.getBody();
            return responseBody;
        } catch (Exception e) {
            // TODO: handle exception
        }

        return "404";
    }

    @GetMapping("getDataForLeadTime")
    public String getDataForLeadTime(@RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("projectSlug") String projectSlug, @RequestParam("sprintId") int sprintId) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        String url = LEADTIME_URL + "/api/getDataForLeadTime?projectSlug=" + projectSlug + "&sprintId=" + sprintId;
        System.out.println("here");
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = responseEntity.getBody();
            return responseBody;
        } catch (Exception e) {
            System.err.println(e);
        }

        return "404";
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
    public String getMilestoneCompletedPoints(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String projectSlug) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        String url = VELOCITY_URL + "/api/" + projectSlug + "/getTotalPoints";
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = responseEntity.getBody();
            return responseBody;
        } catch (Exception e) {
            // TODO: handle exception
        }

        return "404";
        // return milestoneService.getMilestoneTotalPoints(authToken, projectId);

    }

    // Work Capacity
    @GetMapping("/{projectSlug}/getCompletedPoints")
    public List<CompletedPoints> getMilestoneTotalCompletedPoints(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable String projectSlug) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        int projectId = projectService.getProjectId(authToken, projectSlug);

        return milestoneService.getMilestoneCompletedPoints(authToken, projectId);

    }

    public static HashMap<String, ArrayNode> convertJsonToHashMap(String jsonString) {

        HashMap<String, ArrayNode> resultMap = new HashMap<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                ArrayNode arrayNode = objectMapper.createArrayNode();
                for (JsonNode node : entry.getValue()) {
                    arrayNode.add((ObjectNode) node);
                }
                resultMap.put(entry.getKey(), arrayNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    @GetMapping("/{projectSlug}/multiSprintBundown")
    public HashMap<String, ArrayNode> getmultiSprintBundown(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String projectSlug) {
        // make this exact same call to a service running on a different port:
        // http://localhost:8081/api//{projectSlug}/getMultiSprintBurndown
        // make the api call:
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        String url = BURNDOWN_URL + "/api/" + projectSlug + "/multiSprintBundown";
        // print the url
        System.out.println(url);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authToken)
                .build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());

        String responseBody = response.join().body();

        HashMap<String, ArrayNode> finalResponse = convertJsonToHashMap(responseBody);

        return finalResponse;
    }

    @GetMapping("/{projectSlug}/auc")
    public String getAUC(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String projectSlug) {
        // make this exact same call to a service running on a different port:
        // http://localhost:8081/api//{projectSlug}/getMultiSprintBurndown
        // make the api call:
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        String url = AUC_URL + "/work-auc-data/" + projectSlug + "/" + authToken;
        // print the url
        System.out.println(url);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authToken)
                .build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());

        String responseBody = response.join().body();

        return responseBody;
    }

    @GetMapping("/getLeadTimeForAbitraryTimeframe")

    public String getLeadTimeForAbitraryTimeframe(@RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("projectSlug") String projectSlug, @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) throws ParseException {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        String url = LEADTIME_URL + "/api/getLeadTimeForAbitraryTimeframe?projectSlug=" + projectSlug + "&startDate="
                + startDate + "&endDate=" + endDate;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = responseEntity.getBody();
            return responseBody;
        } catch (Exception e) {
            System.err.println(e);
        }

        return "404";

    }

    @GetMapping("/{milestoneId}/getEstimateEffectiveness")
    public String getEstimateEffectiveness(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable int milestoneId) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        String url = ESTIMATEEFFECTIVENESS_URL + "/api/" + milestoneId + "/getEstimateEffectiveness";
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = responseEntity.getBody();
            return responseBody;
        } catch (Exception e) {
            System.err.println(e);
        }

        return "404";

    }

    @GetMapping("/{projectSlug}/getArbitraryCycleTime")
    public String getCycleTimeForArbitaryTimeFrame(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable String projectSlug,
            @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        // String authToken = authenticationService.getAuthToken(authorizationHeader);
        // int projectId =
        // projectService.getProjectId(authenticationService.getAuthToken(authorizationHeader),
        // projectSlug);
        // return tasksService.getCycleTimeForArbitaryTimeFrame(projectId,
        // authenticationService.getAuthToken(authorizationHeader), startDate, endDate);

        String authToken = authenticationService.getAuthToken(authorizationHeader);
        String url = CYCLETIME_URL + "/api/" + projectSlug + "/getArbitraryCycleTime?startDate=" + startDate
                + "&endDate=" + endDate;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = responseEntity.getBody();
            return responseBody;
        } catch (Exception e) {
            // TODO: handle exception
        }

        return "404";
    }

    //call vip microservice
    @GetMapping("/{projectSlug}/{milestoneId}/vipData")
    public String getVIP(@RequestHeader("Authorization") String authorizationHeader, @PathVariable int milestoneId, @PathVariable String projectSlug) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        String url = VIP_URL + "/VIPC";

        //post request body
        int projectId = projectService.getProjectId(authToken, projectSlug);
        Map<String, String> session = new HashMap<>();
        session.put("auth_token", authToken);
        session.put("project_id", Integer.toString(projectId));
        session.put("sprint_id", Integer.toString(milestoneId));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("session", session);

        //convet request body to json string
        ObjectMapper mapper = new ObjectMapper();
        String requestBodyJson = "";
        try {
            requestBodyJson = mapper.writeValueAsString(requestBody);
        } catch ( IOException e) {
            e.printStackTrace();
        }

        //crate http header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return responseEntity.getBody();


    }


    //call value auc microservice
    @GetMapping("/{projectSlug}/valueAucData")
    public String getValueAucData(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String projectSlug) {
        String authToken = authenticationService.getAuthToken(authorizationHeader);
        String url = VALUE_AUC_URL + "/value-auc-data";

        //post request body
        int projectId = projectService.getProjectId(authToken, projectSlug);
        Map<String, String> session = new HashMap<>();
        session.put("auth_token", authToken);
        session.put("project_id", Integer.toString(projectId));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("session", session);

        //convert request body to json string
        ObjectMapper mapper = new ObjectMapper();
        String requestBodyJson = "";
        try {
            requestBodyJson = mapper.writeValueAsString(requestBody);
        } catch ( IOException e) {
            e.printStackTrace();
        }

        //crate http header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return responseEntity.getBody();
    }

}
