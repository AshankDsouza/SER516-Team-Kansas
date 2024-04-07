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

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TasksService {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();


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


}
