package com.kansas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kansas.model.ArbitaryCycleTime;
import com.kansas.model.CycleTime;
import com.kansas.utils.GlobalData;
import com.kansas.utils.HTTPRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TasksService {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();

    @Autowired
    private  AuthenticationService authenticationService;



    static DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public List<JsonNode> getClosedTasks(int projectId, String authToken) {

        // API to get list of all tasks in a project.
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

    private LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    private int[] calculateCycleTime(JsonNode historyData, LocalDateTime finishedDate) {
        int cycleTime = 0;
        int closedTasks = 0;

        for (JsonNode event : historyData) {
            JsonNode valuesDiff = event.get("values_diff");
            if (valuesDiff != null && valuesDiff.has("status")) {
                JsonNode statusDiff = valuesDiff.get("status");
                if (statusDiff.isArray() && statusDiff.size() == 2
                        && "New".equals(statusDiff.get(0).asText()) && "In progress".equals(statusDiff.get(1).asText())) {
                    LocalDateTime createdAt =parseDateTime(event.get("created_at").asText());
                    cycleTime += Duration.between(createdAt.toLocalDate().atStartOfDay(), finishedDate.toLocalDate().atStartOfDay()).toDays();
                    closedTasks++;
                }
            }
        }

        return new int[]{cycleTime, closedTasks};
    }

    private int[] calculateCycleTime(JsonNode historyData, LocalDate startDate, LocalDate endDate,LocalDate finishedDate) {
        int cycleTime = 0;
        int closedTasks = 0;

        for (JsonNode event : historyData) {
            JsonNode valuesDiff = event.get("values_diff");
            if (valuesDiff != null && valuesDiff.has("status")) {
                JsonNode statusDiff = valuesDiff.get("status");
                if (statusDiff.isArray() && statusDiff.size() == 2
                        && "New".equals(statusDiff.get(0).asText()) && "In progress".equals(statusDiff.get(1).asText())) {
                    LocalDate createdAt = LocalDate.parse(pattern.format(parseDateTime(event.get("created_at").asText())));
                    if(startDate.isBefore(createdAt) && finishedDate.isBefore(endDate)) {
                        cycleTime += Duration.between(createdAt.atStartOfDay(), finishedDate.atStartOfDay()).toDays();
                        closedTasks++;
                    }
                }
            }
        }

        return new int[]{cycleTime, closedTasks};
    }

    public List<CycleTime> getTaskHistory(int projectId, int milestoneId, String authToken) {
        List<CycleTime> result = new ArrayList<>();
        List<JsonNode> tasks =  getClosedTasks(projectId, authToken);
        for (JsonNode task : tasks) {
            int taskId = task.get("id").asInt();
            int milestone = task.get("milestone").asInt();

            if(milestone == milestoneId){
                String taskHistoryUrl = TAIGA_API_ENDPOINT + "/history/task/" + taskId;
                try {
                    HttpGet request = new HttpGet(taskHistoryUrl);
                    request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
                    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

                    String responseJson = HTTPRequest.sendHttpRequest(request);
                    JsonNode historyData = objectMapper.readTree(responseJson);

                    LocalDateTime finishedDate = parseDateTime(task.get("finished_date").asText());

                    int[] cycleTimeAndClosedTasks = calculateCycleTime(historyData, finishedDate);
                    String taskName = task.get("subject").toString();
                    result.add(new CycleTime(taskName,cycleTimeAndClosedTasks[0],cycleTimeAndClosedTasks[1]));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }




    public List<ArbitaryCycleTime> getCycleTimeForArbitaryTimeFrame(int projectId, String authToken,String startDate, String endDate) {
        List<JsonNode> tasks =  getClosedTasks(projectId, authToken);
        int totalCycleTime = 0;
        int noOfClosedTasks = 0;
        List<ArbitaryCycleTime> result = new ArrayList<>();
        for (JsonNode task : tasks) {
            int taskId = task.get("id").asInt();

            String taskHistoryUrl = TAIGA_API_ENDPOINT + "/history/task/" + taskId;
            try {
                HttpGet request = new HttpGet(taskHistoryUrl);
                request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
                request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

                String responseJson = HTTPRequest.sendHttpRequest(request);
                JsonNode historyData = objectMapper.readTree(responseJson);

                LocalDate finishedDate = LocalDate.parse(pattern.format(parseDateTime(task.get("finished_date").asText())));

                LocalDate startDateTime = LocalDate.parse(startDate);
                LocalDate endDateTime = LocalDate.parse(endDate);

                int[] cycleTimeAndClosedTasks = calculateCycleTime(historyData,startDateTime, endDateTime,finishedDate);

                String taskName = task.get("subject").asText();
                if(cycleTimeAndClosedTasks[0] != 0){
                    result.add(new ArbitaryCycleTime(taskName,cycleTimeAndClosedTasks[0]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}
