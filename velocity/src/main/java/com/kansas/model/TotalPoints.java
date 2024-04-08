package com.kansas.model;

public class TotalPoints {
    private String sprintName;
    private int totalPoints;

    public TotalPoints(String sprintName, int totalPoints) {
        this.sprintName = sprintName;
        this.totalPoints = totalPoints;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setCompletedPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

}
