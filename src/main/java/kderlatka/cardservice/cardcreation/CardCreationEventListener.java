package kderlatka.cardservice.cardcreation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kderlatka.cardservice.shared.event.domain.CardRegistrationFailedEvent;
import kderlatka.cardservice.shared.event.domain.CardReadyForUseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardCreationEventListener {

    private final CardCreationService cardCreationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "card-registration-failed",
            groupId = "card-creation-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onCardRegistrationFailed(String message, Acknowledgment ack) {
        try {
            CardRegistrationFailedEvent event =
                    objectMapper.readValue(message, CardRegistrationFailedEvent.class);

            log.info("Processing CardRegistrationFailed event: {} (reason: {})",
                    event.getProspectCardId(), event.getReason());

            cardCreationService.markCardAsFailed(event);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing CardRegistrationFailed event", e);
        }
    }

    @KafkaListener(
            topics = "card-ready",
            groupId = "card-creation-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onCardReadyForUse(String message, Acknowledgment ack) {
        try {
            CardReadyForUseEvent event =
                    objectMapper.readValue(message, CardReadyForUseEvent.class);

            log.info("Processing CardReadyForUseEvent event: {} (prospectId: {})",
                    event.getProspectCardId(), event.getProspectCardId());

            cardCreationService.markCardAsReadyForUse(event);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing CardReadyForUseEvent event", e);
        }
    }

}
