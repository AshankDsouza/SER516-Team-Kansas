package com.kansas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.kansas.utils.GlobalData;
import com.kansas.utils.HTTPRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Service;
import com.kansas.model.EffectivenessEstimatePoints;
import java.time.format.DateTimeFormatter;
import com.kansas.service.MilestoneService;
import java.util.*;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TasksService {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();

    private MilestoneService milestoneService;

    static DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public List<JsonNode> getClosedTasks(int projectId, String authToken) {

        String endpoint = TAIGA_API_ENDPOINT + "/tasks?project=" + projectId;

        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String responseJson = HTTPRequest.sendHttpRequest(request);

        try {
            JsonNode tasksNode = objectMapper.readTree(responseJson);
            List<JsonNode> closedTasks = new ArrayList<>();

            for (JsonNode taskNode : tasksNode) {
                boolean isClosed = taskNode.has("is_closed") && taskNode.get("is_closed").asBoolean();
                if (isClosed) {
                    closedTasks.add(taskNode);
                }
            }

            return closedTasks;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<EffectivenessEstimatePoints> calculateEstimateEffectiveness(int milestoneId, String authToken){
        List<EffectivenessEstimatePoints> effectiveEstimatePointsList = new ArrayList<>();

        JsonNode milestoneData = milestoneService.getMilestoneData(authToken, milestoneId);
        int totalCycleTime = 0;
        int totalStoryPoints = 0;
        if (milestoneData != null) {
            JsonNode userStories = milestoneData.get("user_stories");
            if (userStories != null && userStories.isArray()) {
                for (JsonNode userStory : userStories) {
                    int cycleTime = 0;
                    int storyPoints = userStory.get("total_points").asInt();
                    if(userStory.get("is_closed").asBoolean()){
                        LocalDateTime finishedDate = parseDateTime(userStory.get("finish_date").asText());
                        LocalDateTime createdDate = parseDateTime(userStory.get("created_date").asText());
                        cycleTime += Duration.between(createdDate.toLocalDate().atStartOfDay(), finishedDate.toLocalDate().atStartOfDay()).toDays();
                    }
                    totalCycleTime += cycleTime;
                    totalStoryPoints += storyPoints;
                }
                for (JsonNode userStory : userStories) {
                    int cycleTime = 0;
                    int storyPoints = userStory.get("total_points").asInt();
                    String storyTitle = userStory.get("subject").asText();
                    if(userStory.get("is_closed").asBoolean()){
                        LocalDateTime finishedDate = parseDateTime(userStory.get("finish_date").asText());
                        LocalDateTime createdDate = parseDateTime(userStory.get("created_date").asText());
                        cycleTime += Duration.between(createdDate.toLocalDate().atStartOfDay(), finishedDate.toLocalDate().atStartOfDay()).toDays();
                    }
                    double cycleTimeRatio = (double) cycleTime / totalCycleTime;
                    double storyPointsRatio = (double) storyPoints / totalStoryPoints;
                    double effectiveness = cycleTimeRatio / storyPointsRatio;
                    effectiveEstimatePointsList.add(new EffectivenessEstimatePoints(storyTitle, effectiveness));
                }

            } else {
                System.out.println("No user stories found for the milestone.");
            }
        } else {
            System.out.println("Failed to retrieve milestone data.");
        }
        return effectiveEstimatePointsList;
    }


}
