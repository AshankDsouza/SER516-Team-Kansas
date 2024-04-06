package model;

public class CompletedPoints {

    private String sprintName;
    private int completedPoints;

    public CompletedPoints(String sprintName, int completedPoints) {
        this.sprintName = sprintName;
        this.completedPoints = completedPoints;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public int getCompletedPoints() {
        return completedPoints;
    }

    public void setCompletedPoints(int completedPoints) {
        this.completedPoints = completedPoints;
    }


}
