package kderlatka.cardservice.cardcreation;

import kderlatka.cardservice.shared.event.domain.CardRegistrationFailedEvent;
import kderlatka.cardservice.shared.event.domain.CardCreationRequestedEvent;
import kderlatka.cardservice.shared.event.domain.CardReadyForUseEvent;
import kderlatka.cardservice.shared.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
class CardCreationService {

    private final CardProspectRepository cardProspectRepository;
    private final EventPublisher eventPublisher;

    CardProspect createCardProspect(CardCreateRequest request) {
        CardProspect saved = saveCardInPendingState(request);
        emitCardCreationRequestedEvent(request, saved);
        return saved;
    }

    private CardProspect saveCardInPendingState(CardCreateRequest request) {
        CardProspect savedCard = cardProspectRepository.save(request.toCardProspect());
        log.info("Card prospect created with status {}: {}", CardProspectStatus.REGISTERED, savedCard.getProspectId());
        return savedCard;
    }

    private void emitCardCreationRequestedEvent(CardCreateRequest request,
                                                CardProspect savedCard) {
        CardCreationRequestedEvent event = CardCreationRequestedEvent.builder()
                .prospectCardId(savedCard.getProspectId())
                .correlationId(savedCard.getCorrelationId())
                .cardholderName(request.getCardholderName())
                .scheme(request.getCardScheme())
                .source("card-creation-service")
                .build();

        eventPublisher.publish(event);
    }

    void markCardAsFailed(CardRegistrationFailedEvent event) {
        CardProspect prospect = cardProspectRepository.findByIdOrThrow(event.getProspectCardId());
        prospect.setStatus(CardProspectStatus.FAILED);
        cardProspectRepository.save(prospect);
    }

    void markCardAsReadyForUse(CardReadyForUseEvent event) {
        CardProspect prospect = cardProspectRepository.findByIdOrThrow(event.getProspectCardId());
        prospect.setCardId(event.getCardId());
        prospect.setStatus(CardProspectStatus.CREATED);
        cardProspectRepository.save(prospect);
    }
}
