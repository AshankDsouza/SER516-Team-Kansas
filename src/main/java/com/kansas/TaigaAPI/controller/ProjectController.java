package com.kansas.TaigaAPI.controller;

import com.kansas.TaigaAPI.service.AuthenticationService;
import com.kansas.TaigaAPI.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project")

public class ProjectController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/by_slug/slug={slug}")
    public Integer getProjectDetailsBySlug(@PathVariable String slug) {
        return projectService.getProjectId(authenticationService.getAuthToken(), slug);
    }
}
