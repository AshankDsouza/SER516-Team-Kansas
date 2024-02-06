package com.kansas.TaigaAPI.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TaigaAPI {
    @GetMapping("/")
    public String hello(){
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
}
