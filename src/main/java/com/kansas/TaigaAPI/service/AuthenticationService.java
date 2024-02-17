package com.kansas.TaigaAPI.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kansas.TaigaAPI.TaigaApiApplication;
import com.kansas.TaigaAPI.utils.GlobalData;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@Log4j2
public class AuthenticationService {
    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();
    private static String authToken;

    public String authenticate(String username, String password) throws Exception {

        // Endpoint to authenticate taiga's username and password
        String authEndpoint = TAIGA_API_ENDPOINT + "/auth";

        // Making an API call
        HttpPost request = new HttpPost(authEndpoint);
        String payload = "{\"type\":\"normal\",\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        request.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpResponse response = httpClient.execute(request);

            if(response.getStatusLine().getStatusCode()==401){
                throw new Exception(response.getStatusLine().getReasonPhrase());
            }


            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return parseAuthToken(result.toString());
        } catch (IOException e) {
//            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String parseAuthToken(String responseJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseJson);
            authToken = rootNode.path("auth_token").asText();
            return authToken;
        } catch (Exception e) {
//            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String getAuthToken() {
        return authToken;
    }
}

