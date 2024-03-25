package com.kansas.TaigaAPI.model;

import lombok.Data;

@Data
public class ArbitaryCycleTime {

    private String taskName;
    private int cycleTime;

    public ArbitaryCycleTime(String taskName, int cycleTime) {
        this.taskName = taskName;
        this.cycleTime = cycleTime;
    }

}
