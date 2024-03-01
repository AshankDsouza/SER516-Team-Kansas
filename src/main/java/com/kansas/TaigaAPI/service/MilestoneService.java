package com.kansas.TaigaAPI.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kansas.TaigaAPI.model.CompletedPoints;
import com.kansas.TaigaAPI.utils.GlobalData;
import com.kansas.TaigaAPI.utils.HTTPRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Service;
import com.kansas.TaigaAPI.model.TotalPoints;

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

    public List<TotalPoints>  getMilestoneTotalPoints(String authToken, int projectId){
        JsonNode milestoneList = getMilestoneList(authToken,projectId);
        List<TotalPoints> totalPointsList = new ArrayList<>();
        for(JsonNode milestone: milestoneList){
            if(milestone.get("id") != null){
                JsonNode milestoneData = getMilestoneData(authToken, milestone.get("id").asInt());
                int totalPoints = 0;
                if(!milestoneData.get("total_points").isNull()){
                    totalPoints = milestoneData.get("total_points").asInt();

                }
                totalPointsList.add(new TotalPoints(String.valueOf(milestone.get("name").asText()), totalPoints));
            }
        }
        return totalPointsList;
    }


    public List<CompletedPoints> getMilestoneCompletedPoints(String authToken,int projectId){
        List<CompletedPoints> completedPointsList = new ArrayList<>();
        JsonNode milestoneList = getMilestoneList(authToken,projectId);
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

}