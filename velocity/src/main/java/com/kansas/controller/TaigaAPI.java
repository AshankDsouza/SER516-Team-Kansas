package com.kansas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class TaigaAPI {
    @GetMapping("/")
    public String hello() {
        return "Hello";
    }

    @GetMapping("/openUserStories")
    public String getOpenUserStories() {
        return "Open user stories data";
    }

    @GetMapping("/closedTasksPerWeek")
    public String getClosedTasksPerWeek() {
        return "Closed tasks per week data";
    }

    @GetMapping("/selectProjectsForMetrics")
    public String[] getProjectsForMetrics(@RequestParam String metric) {
        if (metric.equals("burndown")) {
            String[] projects = {"nicolasrmz-devgym", "whrisurdicky-sim2023q2-spanners",
                    "the-princess-bride", "a21asmchaben-project-hunt", "vitao-locacao-de-veiculos"};
            return projects;
        } else if (metric.equals("leadtime")) {
            String[] projects = {"posh", "whrisurdicky-sim2023q2-spanners",
                    "the-princess-bride", "a21asmchaben-project-hunt", "vitao-locacao-de-veiculos"};
            return projects;

        }
        return null;
    }

    @GetMapping("/selectProjectsForVisulaisation")
    public String getProjectForVisulaisation(@RequestParam String project) {
        String[] selectedProjects = {"the-princess-bride", "melodicpinpon-z-anatomy", "transformap", "penpot", "viya-mdn-durable-team"};
        for (int i = 0; i < selectedProjects.length; i++) {
            if (project.equals(selectedProjects[i])) {
                return "You have selected Project No: " + (i + 1) ;
            }
        }
        return "Please choose a Project";
    }
}
