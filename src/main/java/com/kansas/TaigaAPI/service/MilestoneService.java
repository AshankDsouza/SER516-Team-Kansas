package com.kansas.TaigaAPI.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kansas.TaigaAPI.utils.GlobalData;
import com.kansas.TaigaAPI.utils.HTTPRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class MilestoneService {

    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public JsonNode getBurnDownMetrics(String authToken,int mileStoneId) {
        try {
            String endpoint = TAIGA_API_ENDPOINT + "/milestones/" + mileStoneId+"/stats";
            HttpGet request = new HttpGet(endpoint);
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            String responseJson = HTTPRequest.sendHttpRequest(request);

            return objectMapper.readTree(responseJson).get("days");
        } catch(Exception e) {

          //  log.error(e.getMessage());
            return null;
        }
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

    public int getMilestoneIdFromName(JsonNode mileStoneList, String milestoneName) {
        for(JsonNode milestone: mileStoneList) {
            if(milestone.has("name") && milestone.get("name").equals(milestoneName)) {
                return Integer.parseInt(milestone.get("id").toString());
            }
        }
        return -1;
    }
}
