package mba.usp.monolith.messaging;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import mba.usp.monolith.services.MqttReceiverService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.concurrent.TimeUnit;

@Service
public class MqttListenerService {
    private final MqttClient mqttClient;
    private final Counter messageCounter;
    private final Timer processingTime;
    private final MqttReceiverService mqttReceiverService;


    public MqttListenerService(MqttClient mqttClient, MeterRegistry registry, MqttReceiverService mqttReceiverService) {
        this.mqttClient = mqttClient;
        this.messageCounter = Counter.builder("mqtt_messages_received")
                .description("Number of MQTT messages received")
                .register(registry);
        this.processingTime = Timer.builder("processing_time")
                .tag("service","monolith")
                .description("Time taken to process MQTT messages")
                .register(registry);
        this.mqttReceiverService = mqttReceiverService;
    }

    @PostConstruct
    public void subscribe() throws Exception {
        mqttClient.subscribe("#", (topic, message) -> {
            long start = System.currentTimeMillis();
            String payload = new String(message.getPayload());
            messageCounter.increment();

            mqttReceiverService.handleMessage(topic,message);

            processingTime.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
        });
    }
}
