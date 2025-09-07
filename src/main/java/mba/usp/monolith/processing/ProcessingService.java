package mba.usp.monolith.processing;

import mba.usp.monolith.domain.repositories.SensorDataRepository;
import mba.usp.monolith.processing.analytics.MovingAverage;
import mba.usp.monolith.processing.analytics.AnomalyDetector;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProcessingService {

    private final SensorDataRepository repository;
    private final Map<String, MovingAverage> movingAverages = new HashMap<>();
    private final AnomalyDetector anomalyDetector = new AnomalyDetector();

    private static final int WINDOW_SIZE = 10;
    private static final double STABILITY_THRESHOLD = 2.0;

    public ProcessingService(SensorDataRepository repository) {
        this.repository = repository;

        movingAverages.put("temperature", new MovingAverage(WINDOW_SIZE));
        movingAverages.put("humidity", new MovingAverage(WINDOW_SIZE));
        movingAverages.put("pressure", new MovingAverage(WINDOW_SIZE));
    }

    public void process(SensorData data) {
        System.out.println("🔍 Processando dados: " + data);

        double tempAvg = movingAverages.get("temperature").add(data.getTemperature());
        double humAvg = movingAverages.get("humidity").add(data.getHumidity());
        double presAvg = movingAverages.get("pressure").add(data.getPressure());

        if (!movingAverages.get("temperature").isStable(STABILITY_THRESHOLD)) {
            System.out.println("⚠️ Temperatura instável! Média: " + tempAvg);
        }

        if (!movingAverages.get("humidity").isStable(STABILITY_THRESHOLD)) {
            System.out.println("⚠️ Umidade instável! Média: " + humAvg);
        }

        if (!movingAverages.get("pressure").isStable(STABILITY_THRESHOLD)) {
            System.out.println("⚠️ Pressão instável! Média: " + presAvg);
        }

        List<String> anomalies = anomalyDetector.detect(data);
        if (!anomalies.isEmpty()) {
            anomalies.forEach((anomaly) -> {
                System.out.println("🚨 Anomalia detectada: " + anomaly);
            });
        }

        // 🧠 Persistência dos dados
        repository.save(data);
    }
}
