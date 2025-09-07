package mba.usp.monolith.ingestion.mqtt;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.concurrent.TimeUnit;

@Service
public class MqttListenerService {
    private final MqttClient mqttClient;
    private final Counter messageCounter;
    private final Timer processingTime;
    private final MqttMessageHandler mqttMessageHandler;


    public MqttListenerService(MqttClient mqttClient, MeterRegistry registry, MqttMessageHandler mqttMessageHandler) {
        this.mqttClient = mqttClient;
        this.messageCounter = Counter.builder("mqtt_messages_received")
                .description("Number of MQTT messages received")
                .register(registry);
        this.processingTime = Timer.builder("mqtt_message_processing_time")
                .description("Time taken to process MQTT messages")
                .register(registry);
        this.mqttMessageHandler = mqttMessageHandler;
    }

    @PostConstruct
    public void subscribe() throws Exception {
        mqttClient.subscribe("#", (topic, message) -> {
            long start = System.nanoTime();
            String payload = new String(message.getPayload());
            messageCounter.increment();  // Track message count

            mqttMessageHandler.handleMessage(topic,message);

            processingTime.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        });
    }
}
