package com.kansas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kansas.utils.GlobalData;
import com.kansas.utils.HTTPRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {

    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();
    private static final Scanner scanner = new Scanner(System.in);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static void main(String[] args) {

    }

    private static String promptUser(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static String promptUserPassword(String prompt) {
        if (System.console() != null) {
            char[] passwordChars = System.console().readPassword(prompt);
            return new String(passwordChars);
        } else {
            System.out.print(prompt);
            return scanner.nextLine();
        }
    }

    private static void handleUserAction(int projectId, String authToken, Scanner scanner) {

    }

    private static void getOpenUserStories(int projectId, String authToken) {

        // Taiga endpoint to get list of all open user stories.
        String endpoint = TAIGA_API_ENDPOINT + "/userstories?project=" + projectId;

        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        // Making an API call with the above endpoint and getting the response.
        String responseJson = HTTPRequest.sendHttpRequest(request);

        try {
            JsonNode userStoriesNode = objectMapper.readTree(responseJson);
            List<String> openUserStories = new ArrayList<>();

            for (JsonNode storyNode : userStoriesNode) {
                boolean isClosed = storyNode.has("is_closed") && storyNode.get("is_closed").asBoolean();
                if (!isClosed) {
                    String name = storyNode.has("subject") ? storyNode.get("subject").asText() : "";
                    String description = storyNode.has("description") ? storyNode.get("description").asText() : "";

                    String storyDetails = String.format("{ \"name\": \"%s\", \"description\": \"%s\" }", name, description);
                    openUserStories.add(storyDetails);
                }
            }

            String result = String.format("{ \"open_user_stories\": [%s] }", String.join(", ", openUserStories));
            ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            String formattedJson = objectMapper.writeValueAsString(objectMapper.readTree(result));
            System.out.println(formattedJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return LocalDateTime.parse(dateTimeString, formatter);
    }




}

