package mba.usp.monolith.services;

import mba.usp.monolith.models.SensorData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnomalyDetector {

    public List<String> detect(SensorData data) {
        List<String> alerts = new ArrayList<>();

        boolean isNight = isNightTime(data.getDateTime());

        // Som
        double amplitude = data.getSound().getAmplitude();
        double maxFreq = maxFrequency(data);

        if (isNight) {
            if (amplitude > 60 || maxFreq > 70) {
                alerts.add("🔊 Ruído elevado durante o período noturno detectado.");
            }
        } else {
            if (amplitude > 80 || maxFreq > 85) {
                alerts.add("🔊 Nível de som elevado detectado.");
            }
        }

        // Temperatura
        if (data.getTemperature() < 10 || data.getTemperature() > 35) {
            alerts.add("🌡️ Temperatura fora do intervalo seguro.");
        }

        // Umidade
        if (data.getHumidity() < 20 || data.getHumidity() > 80) {
            alerts.add("💧 Umidade fora do intervalo recomendado.");
        }

        // Gases
        if (data.getOxidised() > 2.0) {
            alerts.add("🧪 Nível elevado de gás oxidante detectado.");
        }

        if (data.getReduced() > 1000) {
            alerts.add("🧪 Nível elevado de gás redutor detectado.");
        }

        if (data.getNh3() > 5.0) {
            alerts.add("🧪 Concentração elevada de amônia (NH3).");
        }

        if (data.getPressure() < 950 || data.getPressure() > 1050) {
            alerts.add("📈 Pressão atmosférica fora do intervalo esperado (950-1050 hPa).");
        }

        // Luz noturna
        if (data.getLight() > 900 && isNight) {
            alerts.add("💡 Luminosidade alta durante o período noturno.");
        }

        // Proximidade
        if (data.getProximity() < 0 || data.getProximity() > 100) {
            alerts.add("📏 Objeto em distância fora do intervalo esperado.");
        }

        return alerts;
    }

    private boolean isNightTime(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        return time.isAfter(LocalTime.of(22, 0)) || time.isBefore(LocalTime.of(6, 0));
    }

    private double maxFrequency(SensorData data) {
        return Math.max(data.getSound().getHigh(),
                Math.max(data.getSound().getMid(), data.getSound().getLow()));
    }
}