package com.kansas.TaigaAPI.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kansas.TaigaAPI.TaigaApiApplication;
import com.kansas.TaigaAPI.model.CycleTime;
import com.kansas.TaigaAPI.utils.GlobalData;
import com.kansas.TaigaAPI.utils.HTTPRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TasksService {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();

    @Autowired
    private  AuthenticationService authenticationService;

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
                 //   System.out.println(historyData);
                    LocalDateTime finishedDate = parseDateTime(task.get("finished_date").asText());

                    int[] cycleTimeAndClosedTasks = calculateCycleTime(historyData, finishedDate);
                    String taskName = task.get("subject").toString();
                    result.add(new CycleTime(taskName,cycleTimeAndClosedTasks[0],cycleTimeAndClosedTasks[1]));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
            // API to get history of task
        return result;
    }


    public static HashMap getTasksClosedByDate(int projectId, int sprintId, String authToken) {
        try{


            String sprintTasksUrl =  TAIGA_API_ENDPOINT + "/tasks?project=" + projectId + "&milestone=" + sprintId;
            HttpGet request = new HttpGet(sprintTasksUrl);
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            String responseJson = HTTPRequest.sendHttpRequest(request);
            JsonNode taskData = objectMapper.readTree(responseJson);
            if (taskData.get("code") == null){}
            else if (taskData.get("code").asText().equals("token_not_valid")){
                HashMap error = new HashMap<>();
                error.put("error","Token not valid");
                return error;
            }

            LocalDate start = LocalDate.parse(taskData.get(0).get("created_date").toString().substring(1,11));
            LocalDate end= LocalDate.now();

            List<LocalDate> totalDates = new ArrayList<>();
            while (!start.isAfter(end)) {
                totalDates.add(start);
                start = start.plusDays(1);
            }
            HashMap closedTasksByDay = new HashMap<>();
            for (JsonNode taskNode : taskData) {
                boolean isClosed = taskNode.has("is_closed") && taskNode.get("is_closed").asBoolean();
                if (isClosed) {
                    LocalDate date = LocalDate.parse(taskNode.get("finished_date").toString().substring(1,11));
                    if (closedTasksByDay.containsKey(date)){
                        closedTasksByDay.put(date, Integer.parseInt(closedTasksByDay.get(date).toString()) + 1);
                    }
                    else {
                        closedTasksByDay.put(date, 1);
                    }
                }
            }
            for (LocalDate remDate : totalDates){
                if (closedTasksByDay.containsKey(remDate)){}
                else{
                closedTasksByDay.put(remDate,0);}
            }
            return closedTasksByDay;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static HashMap getWorkCapacityBySprint(int projectId, int sprintId, String authToken) {
        try{


            String sprintTasksUrl =  TAIGA_API_ENDPOINT + "/tasks?project=" + projectId + "&milestone=" + sprintId;
            HttpGet request = new HttpGet(sprintTasksUrl);
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            String responseJson = HTTPRequest.sendHttpRequest(request);
            JsonNode taskData = objectMapper.readTree(responseJson);
            if (taskData.get("code") == null){}
            else if (taskData.get("code").asText().equals("token_not_valid")){
                HashMap error = new HashMap<>();
                error.put("error","Token not valid");
                return error;
            }

            LocalDate start = LocalDate.parse(taskData.get(0).get("created_date").toString().substring(1,11));
            LocalDate end= LocalDate.now();

            List<LocalDate> totalDates = new ArrayList<>();
            while (!start.isAfter(end)) {
                totalDates.add(start);
                start = start.plusDays(1);
            }
            HashMap closedTasksByDay = new HashMap<>();
            for (JsonNode taskNode : taskData) {
                boolean isClosed = taskNode.has("is_closed") && taskNode.get("is_closed").asBoolean();
                if (isClosed) {
                    LocalDate date = LocalDate.parse(taskNode.get("finished_date").toString().substring(1,11));
                    if (closedTasksByDay.containsKey(date)){
                        closedTasksByDay.put(date, Integer.parseInt(closedTasksByDay.get(date).toString()) + 1);
                    }
                    else {
                        closedTasksByDay.put(date, 1);
                    }
                }
            }
            for (LocalDate remDate : totalDates){
                if (closedTasksByDay.containsKey(remDate)){}
                else{
                    closedTasksByDay.put(remDate,0);}
            }
            return closedTasksByDay;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }


}
