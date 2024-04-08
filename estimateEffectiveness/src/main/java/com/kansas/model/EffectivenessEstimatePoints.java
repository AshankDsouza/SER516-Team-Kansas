package com.kansas.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class EffectivenessEstimatePoints {
    private String taskId;
    private double estimateAccuracy;

    public EffectivenessEstimatePoints(String taskId, double estimateAccuracy) {
        this.taskId = taskId;
        this.estimateAccuracy = estimateAccuracy;
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public double getEstimateAccuracy() {
        return estimateAccuracy;
    }

    public void setEstimateAccuracy(double estimateAccuracy) {
        this.estimateAccuracy = estimateAccuracy;
    }
}
