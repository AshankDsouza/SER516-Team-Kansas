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

import java.util.HashMap;
import java.util.Scanner;


@Service
public class ProjectService {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
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

    public int getProjectId(String authToken,String projectSlug) {

        // Prompting user to enter project slug name. A slug name is nothing but an identifier for a project.
        // Open any Taiga project and check the url of your browser. Slug name is the value after " /project/SLUG_NAME "
        // Example https://tree.taiga.io/project/SLUG_NAME/us/1?no-milestone=1

        String responseJson = getProjectdetails(authToken,projectSlug);
        System.out.println(responseJson);
        if (responseJson != null) {
            try {
                JsonNode projectInfo = objectMapper.readTree(responseJson);
                int projectId = projectInfo.has("id") ? projectInfo.get("id").asInt() : -1;

                if (projectId != -1) {
                    System.out.println("Project details retrieved successfully.");
                    return projectId;
                } else {
                    System.out.println("Invalid project slug. Please try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    public HashMap getprojectIdAndSprintId(String authToken,String projectSlug){
        String responseJson = getProjectdetails(authToken,projectSlug);
        HashMap result = new HashMap<>();
        if (responseJson != null) {
            try {
                JsonNode projectInfo = objectMapper.readTree(responseJson);
                int projectId = projectInfo.has("id") ? projectInfo.get("id").asInt() : -1;
                JsonNode milestoneIds =projectInfo.has("milestones") ? projectInfo.get("milestones"): null;
                if (projectId != -1) {
                    System.out.println("Project details retrieved successfully.");
                    result.put(projectId,milestoneIds);
                    return result;
                } else {
                    System.out.println("Invalid project slug. Please try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getProjectdetails(String authToken,String projectSlug){
        String endpoint = TAIGA_API_ENDPOINT + "/projects/by_slug?slug=" + projectSlug;

        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String responseJson = HTTPRequest.sendHttpRequest(request);
        return responseJson;
    }

}

