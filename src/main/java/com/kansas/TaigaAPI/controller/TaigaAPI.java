package com.kansas.TaigaAPI.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

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
        }
        return null;
    }


}
