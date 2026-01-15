package kderlatka.cardservice.cardmanagement

import kderlatka.cardservice.IntegrationSpec
import kderlatka.cardservice.shared.card.CardScheme
import kderlatka.cardservice.shared.event.domain.CardReadyForUseEvent
import kderlatka.cardservice.shared.event.domain.CardRegisteredWithProviderEvent
import org.springframework.beans.factory.annotation.Autowired

import static kderlatka.cardservice.shared.event.EventPublisher.CARD_REGISTERED_TOPIC

class CardManagementIntegrationSpec extends IntegrationSpec {

    @Autowired
    CardRepository cardRepository


    def "should handle CardRegisteredWithProviderEvent, store card in db and emmit CardReadyForUseEvent"() {
        when: "CardRegisteredWithProviderEvent is published to Kafka"
        CardRegisteredWithProviderEvent cardCreationRequestedEvent = cardCreationRequestEventIsPublished()

        then: "CardReadyForUseEvent was emitted"
        CardReadyForUseEvent cardReadyForUseEvent = cardReadyForUseConsumer.verifyEventPublished(cardCreationRequestedEvent.prospectCardId)

        and: "card was stored in database"
        cardRepository.findByCardNumberOrThrow(cardCreationRequestedEvent.cardNumber)
    }

    private CardRegisteredWithProviderEvent cardCreationRequestEventIsPublished() {
        CardRegisteredWithProviderEvent cardCreationRequestedEvent = new CardRegisteredWithProviderEvent()
        cardCreationRequestedEvent.prospectCardId = UUID.randomUUID()
        cardCreationRequestedEvent.cardholderName = "holder"
        cardCreationRequestedEvent.scheme = CardScheme.VISA
        cardCreationRequestedEvent.correlationId = UUID.randomUUID()
        cardCreationRequestedEvent.eventId = UUID.randomUUID()
        cardCreationRequestedEvent.cvv = "123"
        cardCreationRequestedEvent.cardNumber = "12312341223213213"
        cardCreationRequestedEvent.expiryDate = "12/27"

        publishEvent(cardCreationRequestedEvent, CARD_REGISTERED_TOPIC)
        cardCreationRequestedEvent
    }

}
