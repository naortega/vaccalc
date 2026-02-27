package net.themusicinnoise.vaccalc;

import java.time.LocalDate;

public class PointEngine {
    private double defaultPoints;

    public PointEngine() {
        defaultPoints = 1.0;
    }

    public void setDefaultPoints(float defaultPoints) { this.defaultPoints = defaultPoints; }

    public double getPointsOfDay(LocalDate date) {
        return defaultPoints;
    }
}
