package com.kansas.TaigaAPI.controller;

import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")

public class ProjectController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/by_slug/slug={slug}")
    public Integer getProjectDetailsBySlug(@RequestHeader("Authorization") String authorizationHeader,@PathVariable String slug) {
        return projectService.getProjectId(authenticationService.getAuthToken(authorizationHeader), slug);
    }
}
