package com.kansas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kansas.model.CompletedPoints;
import com.kansas.utils.GlobalData;
import com.kansas.utils.HTTPRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class MilestoneService {

    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();


    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public JsonNode getBurnDownMetrics(String authToken,int mileStoneId) {
        JsonNode responseJson = getMilestoneStats(authToken,mileStoneId);
        return responseJson.get("days");
    }

    public JsonNode getMilestoneList(String authToken, int projectId) {

        try {
            String endpoint = TAIGA_API_ENDPOINT + "/milestones?project=" + projectId;
            HttpGet request = new HttpGet(endpoint);
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            String responseJson = HTTPRequest.sendHttpRequest(request);

            return objectMapper.readTree(responseJson);
        } catch(Exception e) {

            // log.error(e.getMessage());

            return null;
        }
    }



    public List<CompletedPoints> getMilestoneCompletedPoints(String authToken,int projectId){
        JsonNode milestoneList = getMilestoneList(authToken,projectId);
        List<CompletedPoints> completedPointsList = new ArrayList<>();
        for(JsonNode milestone: milestoneList){
            if(milestone.get("id") != null){
                JsonNode milestoneStats = getMilestoneStats(authToken, milestone.get("id").asInt());
                int completedPoints = 0;
                if(milestoneStats.get("completed_points").isArray()){
                    for(JsonNode points : milestoneStats.get("completed_points")) {
                        completedPoints += points.asInt();
                    }
                }
                completedPointsList.add(new CompletedPoints(String.valueOf(milestone.get("name").asText()), completedPoints));
            }
        }
        return completedPointsList;
    }

    public JsonNode getMilestoneStats(String authToken, int milestoneId) {
        try {
            String endpoint = TAIGA_API_ENDPOINT + "/milestones/" + milestoneId + "/stats";
            HttpGet request = new HttpGet(endpoint);
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            return objectMapper.readTree(HTTPRequest.sendHttpRequest(request));
        } catch (Exception e) {
            //  log.error(e.getMessage());
            return null;
        }
    }

    public JsonNode getMilestoneData(String authToken, int milestoneId){
        try {
            String endpoint = TAIGA_API_ENDPOINT + "/milestones/" + milestoneId;

            HttpGet request = new HttpGet(endpoint);
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            return objectMapper.readTree(HTTPRequest.sendHttpRequest(request));
        } catch(Exception e) {
            //  log.error(e.getMessage());
            return null;
        }
    }

    public HashMap<String,ArrayNode> getMultiSprintBurndown(String authToken, int projectId, JsonNode projectDetails){
        HashMap<String,ArrayNode> allSprintBrnd = new HashMap<String,ArrayNode>();
        ArrayNode burndownData = null;
        for(JsonNode milestone: projectDetails){
            if(milestone.get("id") != null){
                JsonNode responseJson = getMilestoneStats(authToken,milestone.get("id").asInt());
                burndownData = extractBurnDountDaycountWise(responseJson.get("days"));
                allSprintBrnd.put(milestone.get("name").asText(),burndownData);
            }
        }
        return allSprintBrnd;
    }

    public ArrayNode extractBurnDountDaycountWise(JsonNode daysData){
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        int dayCount=0;
        if (daysData.isArray()) {

            for (JsonNode elementNode : daysData) {
                dayCount+=1;
                ObjectNode filteredObject = mapper.createObjectNode();
                filteredObject.put("day", dayCount);
                filteredObject.put("open_points", elementNode.get("open_points").asDouble());
                filteredObject.put("optimal_points", elementNode.get("optimal_points").asDouble());
                arrayNode.add(filteredObject);
            }
        }
        return arrayNode;
    }

}