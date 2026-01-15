package kderlatka.cardservice.shared.event;

import kderlatka.cardservice.shared.event.domain.DomainEvent;
import kderlatka.cardservice.shared.outbox.OutboxEvent;
import kderlatka.cardservice.shared.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class EventPublisher {

    public static final String CARD_CREATION_TOPIC = "card-creation-requested";
    public static final String CARD_REGISTERED_TOPIC = "card-registered";
    public static final String CARD_REGISTRATION_FAILED_TOPIC = "card-registration-failed";
    public static final String CARD_READY_TOPIC = "card-ready";
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void publish(DomainEvent event) {
        OutboxEvent outbox = OutboxEvent.from(event);
        outboxRepository.save(outbox);
        log.info("Event saved to outbox: {} (correlationId: {})",
                event.getEventType(), event.getCorrelationId());
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> unpublished = outboxRepository.findByPublishedFalseOrderByCreatedAtAsc();
        if (unpublished.isEmpty()) {
            return;
        }
        log.info("publishing {} unpublished pending events", unpublished.size());
        unpublished.forEach(this::publishEvent);
    }

    private void publishEvent(OutboxEvent event) {
        try {
            String topic = getTopic(event.getEventType());
            Message<String> message = prepareMessage(event, topic);
            sendKafkaMsg(event, message, topic);
        } catch (Exception e) {
            log.error("Error publishing event: {}", event.getId(), e);
        }
    }

    private String getTopic(String eventType) {
        return switch (eventType) {
            case "CardCreationRequested" -> CARD_CREATION_TOPIC;
            case "CardRegisteredWithProvider" -> CARD_REGISTERED_TOPIC;
            case "CardRegistrationFailed" -> CARD_REGISTRATION_FAILED_TOPIC;
            case "CardReadyForUse" -> CARD_READY_TOPIC;
            default -> throw new IllegalArgumentException(
                    "Unknown event type: " + eventType);
        };
    }

    private static Message<String> prepareMessage(OutboxEvent event, String topic) {
        return MessageBuilder
                .withPayload(event.getPayload())
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("eventType", event.getEventType())
                .setHeader("correlationId", event.getCorrelationId().toString())
                .build();
    }

    private void sendKafkaMsg(OutboxEvent event, Message<String> message, String topic) {
        kafkaTemplate.send(message)
                .whenComplete((_, ex) -> {
                    if (ex == null) {
                        event.setPublished(true);
                        event.setPublishedAt(LocalDateTime.now());
                        outboxRepository.save(event);
                        log.info("Event published to Kafka: {} [{}]",
                                event.getEventType(), topic);
                    } else {
                        log.error("Failed to publish event to Kafka: {}",
                                event.getEventType(), ex);
                    }
                });
    }
}
