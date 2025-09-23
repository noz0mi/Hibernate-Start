package org.example.service;

import org.example.dto.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Value("${kafka.topic.user-events:user-events}")
    private String userEventsTopic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserEvent(UserEvent userEvent) {
        try {
            kafkaTemplate.send(userEventsTopic, userEvent.getEmail(), userEvent)
                    .getClass(
                    );
        } catch (Exception e) {
            System.err.println("Error sending message to Kafka: " + e.getMessage());
        }
    }
}
