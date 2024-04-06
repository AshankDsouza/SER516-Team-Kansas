package com.kansas.TaigaAPI.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class EffectiveEstimatePoints {
    private String storyTitle;
    private double effectiveness;

    public EffectiveEstimatePoints(String storyTitle, double effectiveness) {
        this.storyTitle = storyTitle;
        this.effectiveness = effectiveness;
    }
}
