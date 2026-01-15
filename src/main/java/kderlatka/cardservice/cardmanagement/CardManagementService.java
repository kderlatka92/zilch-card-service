package kderlatka.cardservice.cardmanagement;

import kderlatka.cardservice.shared.event.domain.CardRegisteredWithProviderEvent;
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
class CardManagementService {

    private final CardRepository cardRepository;
    private final EventPublisher eventPublisher;

    void activateCard(CardRegisteredWithProviderEvent event) {
        Card updatedCard = registerCard(event);
        emitCardReadyEvent(event, updatedCard);
        log.info("Card activated and ready for use: {}", updatedCard.getCardId());
    }

    private Card registerCard(CardRegisteredWithProviderEvent event) {
        Card card = Card
                .builder()
                .cardNumber(event.getCardNumber())
                .cvv(event.getCvv())
                .expiryDate(event.getExpiryDate())
                .cardType(event.getCardType())
                .status(CardStatus.REGISTERED)
                .cardholderName(event.getCardholderName())
                .build();

        return cardRepository.save(card);
    }

    private void emitCardReadyEvent(CardRegisteredWithProviderEvent event,
                                    Card savedCard) {
        eventPublisher.publish(CardReadyForUseEvent.of(event, savedCard));
    }

}
