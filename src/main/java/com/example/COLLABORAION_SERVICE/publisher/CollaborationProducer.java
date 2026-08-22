package com.example.COLLABORAION_SERVICE.publisher;

import com.example.COLLABORAION_SERVICE.dto.event.CollaborationRequestAcceptedEvent;
import com.example.COLLABORAION_SERVICE.dto.event.CollaborationRequestRejectedEvent;
import com.example.COLLABORAION_SERVICE.dto.event.CollaborationRequestSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollaborationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishRequestSent(
            CollaborationRequestSentEvent event
    ) {
        rabbitTemplate.convertAndSend(
                "collaboration.exchange",
                "notification.collaboration.request",
                event
        );
    }

    public void publishRequestAccepted(
            CollaborationRequestAcceptedEvent event
    ) {
        rabbitTemplate.convertAndSend(
                "collaboration.exchange",
                "notification.collaboration.accepted",
                event
        );
    }

    public void publishRequestRejected(
            CollaborationRequestRejectedEvent event
    ) {
        rabbitTemplate.convertAndSend(
                "collaboration.exchange",
                "notification.collaboration.rejected",
                event
        );
    }
}
