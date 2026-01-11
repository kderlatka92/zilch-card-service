package kderlatka.cardservice

import kderlatka.cardservice.dto.CardCreateRequest
import kderlatka.cardservice.exception.CardNotFoundException
import kderlatka.cardservice.exception.UnrecognisedProviderException
import kderlatka.cardservice.provider.ExternalCardProvider
import spock.lang.Specification

class CardServiceSpec extends Specification {

    CardRepository cardRepository = Mock()
    ExternalCardProvider visaProvider = Mock()
    ExternalCardProvider mcProvider = Mock()

    CardService cardService = new CardService(cardRepository, [visaProvider, mcProvider])

    def "getCard should return card when found in repository"() {
        given:
        def cardNumber = "4111111111111111"
        def card = new Card(cardNumber: cardNumber)
        cardRepository.findByCardNumber(cardNumber) >> Optional.of(card)

        when:
        def result = cardService.getCard(cardNumber)

        then:
        result.is(card)
    }

    def "getCard should throw CardNotFoundException when card does not exist"() {
        given:
        def cardNumber = "9999999999999999"
        cardRepository.findByCardNumber(cardNumber) >> Optional.empty()

        when:
        cardService.getCard(cardNumber)

        then:
        def ex = thrown(CardNotFoundException)
        ex.message == "card with number $cardNumber not found."
    }

    def "createCard should throw UnrecognisedProviderException for not recognized null provider"() {
        when:
        cardService.createCard(new CardCreateRequest())

        then:
        def ex = thrown(UnrecognisedProviderException)
        ex.message.contains("No provider found for scheme")
        0 * cardRepository.save(_)
    }

}
