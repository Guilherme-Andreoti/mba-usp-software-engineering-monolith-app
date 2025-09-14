package mba.usp.monolith.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import mba.usp.monolith.models.SensorData;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class MqttReceiverService {

    private final ObjectMapper objectMapper;
    private final ProcessingService processor;

    public MqttReceiverService(ObjectMapper objectMapper, ProcessingService processor) {
        this.objectMapper = objectMapper;
        this.processor = processor;
    }

    public void  handleMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            if (payload.isBlank()) {
                System.out.println("⚠️ Mensagem vazia recebida no tópico: " + topic);
                return;
            }

            SensorData sensorData = objectMapper.readValue(payload, SensorData.class);
            sensorData.setTopic(topic);

            if (!isValid(sensorData)) {
                System.out.println("⚠️ Dados inválidos recebidos: " + payload);
                return;
            }

            processor.process(sensorData);


        } catch (Exception e) {
            System.err.println("❌ Falha ao processar mensagem MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValid(SensorData data) {
        return data != null
                && data.getDateTime() != null
                && data.getSound() != null;
    }
}