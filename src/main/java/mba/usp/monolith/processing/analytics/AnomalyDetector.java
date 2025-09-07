package mba.usp.monolith.processing.analytics;

import mba.usp.monolith.processing.SensorData;

import java.util.ArrayList;
import java.util.List;

public class AnomalyDetector {

    private static final double TEMP_MIN = 10.0;
    private static final double TEMP_MAX = 40.0;
    private static final double HUMIDITY_MAX = 90.0;
    private static final double PRESSURE_MIN = 950.0;
    private static final double PRESSURE_MAX = 1050.0;
    private static final double SOUND_AMP_MAX = 0.8;

    public List<String> detect(SensorData data) {
        List<String> anomalies = new ArrayList<>();

        if (data.getTemperature() < TEMP_MIN) {
            anomalies.add("Low Temperature");
        } else if (data.getTemperature() > TEMP_MAX) {
            anomalies.add("High Temperature");
        }

        if (data.getHumidity() > HUMIDITY_MAX) {
            anomalies.add("High Humidity");
        }

        if (data.getPressure() < PRESSURE_MIN) {
            anomalies.add("Low Pressure");
        } else if (data.getPressure() > PRESSURE_MAX) {
            anomalies.add("High Pressure");
        }

        if (data.getSound().getAmplitude() > SOUND_AMP_MAX) {
            anomalies.add("High Sound Amplitude");
        }

        return anomalies;
    }
}
