package net.themusicinnoise.vaccalc;

import java.time.LocalDate;

public class PointEngine {
    private double defaultPoints;

    public PointEngine() {
        defaultPoints = 1.0;
    }

    public double getPointsOfDay(LocalDate date) {
        return defaultPoints;
    }
}
