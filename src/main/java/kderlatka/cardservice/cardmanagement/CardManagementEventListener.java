package kderlatka.cardservice.cardmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import kderlatka.cardservice.shared.event.domain.CardRegisteredWithProviderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardManagementEventListener {

    private final CardManagementService cardManagementService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "card-registered",
            groupId = "card-management-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onCardRegistered(String message, Acknowledgment ack) {
        try {
            CardRegisteredWithProviderEvent event =
                    objectMapper.readValue(message, CardRegisteredWithProviderEvent.class);

            log.info("Processing CardRegisteredWithProvider event: {} (correlationId: {})",
                    event.getProspectCardId(), event.getCorrelationId());

            cardManagementService.activateCard(event);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing CardRegisteredWithProvider event", e);
        }
    }

}
