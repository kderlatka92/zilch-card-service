package kderlatka.cardservice.providerintegration;

import kderlatka.cardservice.shared.event.domain.CardCreationRequestedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import kderlatka.cardservice.providerintegration.provider.ProviderIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProviderIntegrationEventListener {

    private final ProviderIntegrationService integrationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "card-creation-requested",
            groupId = "provider-integration-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onCardCreationRequested(String message, Acknowledgment ack) {
        try {
            CardCreationRequestedEvent event =
                    objectMapper.readValue(message, CardCreationRequestedEvent.class);

            log.info("Processing CardCreationRequested event: {} (correlationId: {})",
                    event.getProspectCardId(), event.getCorrelationId());

            integrationService.registerCardWithProvider(event);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing CardCreationRequested event", e);
        }
    }
}
