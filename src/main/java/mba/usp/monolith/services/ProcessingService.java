package mba.usp.monolith.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import mba.usp.monolith.dtos.Notification;
import mba.usp.monolith.models.SensorData;
import mba.usp.monolith.repositories.SensorDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ProcessingService {
    private final AnomalyDetector anomalyDetector;
    private final SensorDataRepository repository;
    private final NotificationHandler notificationHandler;
    private final Counter persistedRecordsCounter;
    private final Timer processingTime;



    public ProcessingService(AnomalyDetector anomalyDetector, NotificationHandler notificationHandler, SensorDataRepository repository,    MeterRegistry registry) {
        this.anomalyDetector = anomalyDetector;
        this.repository = repository;
        this.notificationHandler = notificationHandler;
        this.persistedRecordsCounter = Counter.builder("persisted_records")
                .tag("service","monolith")
                .description("Number of records persisted")
                .register(registry);
        this.processingTime = Timer.builder("processing_time")
                .tag("service","monolith")
                .description("Time taken to process MQTT messages")
                .register(registry);
    }

    public void process(SensorData data) {

        this.anomalyDetector.detect(data);

        List<String> anomalies = this.anomalyDetector.detect(data);

        if (!anomalies.isEmpty()) {
            anomalies.forEach(anomaly -> {
                notificationHandler.sendNotification(new Notification(anomaly, data.getDateTime(), data.getTopic()));
            });
        }

        repository.save(data);

        persistedRecordsCounter.increment();

        processingTime.record(System.currentTimeMillis() - data.getStartProcessingTimestamp(), TimeUnit.MILLISECONDS);
    }
}
