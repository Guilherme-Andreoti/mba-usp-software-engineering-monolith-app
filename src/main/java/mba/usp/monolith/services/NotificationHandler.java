package mba.usp.monolith.services;

import mba.usp.monolith.dtos.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationHandler {
    public void sendNotification(Notification notification){
        System.out.printf(
                "🔔 [Alert] Sensor %s @ %s: %s%n",
                notification.getSensorId(),
                notification.getTimestamp(),
                notification.getMessage()
        );
    }

}