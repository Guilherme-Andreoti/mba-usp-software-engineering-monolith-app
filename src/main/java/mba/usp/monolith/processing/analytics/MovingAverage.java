package mba.usp.monolith.processing.analytics;

import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage {

    private final int windowSize;
    private final Queue<Double> window;
    private double sum;

    public MovingAverage(int windowSize) {
        this.windowSize = windowSize;
        this.window = new LinkedList<>();
        this.sum = 0.0;
    }

    public double add(double newValue) {
        window.add(newValue);
        sum += newValue;

        if (window.size() > windowSize) {
            sum -= window.remove();
        }

        return getAverage();
    }

    public double getAverage() {
        if (window.isEmpty()) {
            return 0.0;
        }
        return sum / window.size();
    }

    public boolean isStable(double threshold) {
        double avg = getAverage();
        for (Double value : window) {
            if (Math.abs(value - avg) > threshold) {
                return false;
            }
        }
        return true;
    }
}
