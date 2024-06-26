package com.kansas.model;

import lombok.Data;


@Data
public class CycleTime {
    private String taskName;
    private int cycleTime;
    private int noOfClosedTasks;


    public CycleTime(String taskName, int cycleTime, int noOfClosedTasks) {
        this.taskName = taskName;
        this.cycleTime = cycleTime;
        this.noOfClosedTasks = noOfClosedTasks;
    }
}
