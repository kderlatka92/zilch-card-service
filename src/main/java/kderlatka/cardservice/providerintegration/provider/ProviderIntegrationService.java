package kderlatka.cardservice.providerintegration.provider;

import kderlatka.cardservice.shared.card.CardScheme;
import kderlatka.cardservice.shared.event.domain.CardCreationRequestedEvent;
import kderlatka.cardservice.providerintegration.ExternalCardData;
import kderlatka.cardservice.shared.event.domain.CardRegisteredWithProviderEvent;
import kderlatka.cardservice.shared.event.domain.CardRegistrationFailedEvent;
import kderlatka.cardservice.shared.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProviderIntegrationService {

    private final List<ExternalCardProvider> externalCardProviders;
    private final EventPublisher eventPublisher;

    public void registerCardWithProvider(CardCreationRequestedEvent event) {
        try {
            ExternalCardProvider provider = selectProvider(event);
            ExternalCardData cardData = provider.generateCard(event);
            emitSuccessEvent(event, cardData);
            log.info("Card registered with provider: {} (correlationId: {})",
                    cardData.getCardNumber(), event.getCorrelationId());
        } catch (Exception e) {
            log.error("Provider integration failed for cardId: {}",
                    event.getProspectCardId(), e);
            emitFailureEvent(event, e);
        }
    }

    private ExternalCardProvider selectProvider(CardCreationRequestedEvent event) {
        CardScheme supportedSchema = event.getScheme();
        return externalCardProviders.stream()
                .filter(provider -> provider.supports(supportedSchema))
                .findFirst()
                .orElseThrow(() -> new InvalidCardProviderException("Provider " + supportedSchema +" is not found among supported providers"));
    }

    private void emitSuccessEvent(CardCreationRequestedEvent event,
                                  ExternalCardData cardData) {
        CardRegisteredWithProviderEvent successEvent =
                CardRegisteredWithProviderEvent.builder()
                        .prospectCardId(event.getProspectCardId())
                        .correlationId(event.getCorrelationId())
                        .cardNumber(cardData.getCardNumber())
                        .cvv(cardData.getCvv())
                        .expiryDate(cardData.getExpiryDate())
                        .cardType(cardData.getCardType())
                        .scheme(event.getScheme())
                        .cardholderName(event.getCardholderName())
                        .source("provider-integration-service")
                        .build();

        eventPublisher.publish(successEvent);
    }

    private void emitFailureEvent(CardCreationRequestedEvent event,
                                  Exception e) {
        CardRegistrationFailedEvent failureEvent =
                CardRegistrationFailedEvent.builder()
                        .prospectCardId(event.getProspectCardId())
                        .correlationId(event.getCorrelationId())
                        .reason(e.getMessage())
                        .errorCode("PROVIDER_ERROR")
                        .source("provider-integration-service")
                        .build();

        eventPublisher.publish(failureEvent);
    }
}
