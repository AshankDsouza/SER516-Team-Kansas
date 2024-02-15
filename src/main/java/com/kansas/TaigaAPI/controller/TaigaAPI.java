package com.kansas.TaigaAPI.controller;

import com.kansas.TaigaAPI.service.TasksService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

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
