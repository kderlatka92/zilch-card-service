package kderlatka.cardservice.providerintegration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import kderlatka.cardservice.IntegrationSpec
import kderlatka.cardservice.providerintegration.provider.mastercard.MasterCardProvider
import kderlatka.cardservice.providerintegration.provider.visa.VisaCardProvider
import kderlatka.cardservice.shared.card.CardScheme
import kderlatka.cardservice.shared.event.domain.CardCreationRequestedEvent
import kderlatka.cardservice.shared.event.domain.CardRegisteredWithProviderEvent
import spock.lang.Shared

import java.util.concurrent.ThreadLocalRandom

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static kderlatka.cardservice.shared.event.EventPublisher.CARD_CREATION_TOPIC

class ProviderIntegrationSpec extends IntegrationSpec {

    private static final String DEFAULT_CVV = "123"
    private static final String DEFAULT_EXPIRY = "12/25"
    private static final String TEST_CARDHOLDER = "Test Cardholder"

    @Shared
    WireMockServer visaMock

    @Shared
    WireMockServer mastercardMock

    def setupSpec() {
        visaMock = createRunWiremock(8081)
        mastercardMock = createRunWiremock(8082)
    }

    def cleanupSpec() {
        visaMock.stop()
        mastercardMock.stop()
    }

    def "should handle CardCreationRequestedEvent for #scheme and get data from #provider provider"() {
        given: "#scheme provider will return card data"
        String generatedCardNumber = schemeReturnsCardData(mockServer, endpoint, TEST_CARDHOLDER)

        when: "CardCreationRequestedEvent with #scheme is published to Kafka"
        CardCreationRequestedEvent cardCreationRequestedEvent = cardCreationRequestEventIsPublished(scheme)

        then: "CardRegisteredWithProviderEvent was emitted with #scheme"
        def event = cardRegisteredWithProviderConsumer.verifyEventPublished(cardCreationRequestedEvent.prospectCardId)
        event.cardholderName == cardCreationRequestedEvent.cardholderName
        event.scheme == scheme

        and: "#provider service was called with correct data"
        mockServer.verify(postRequestedFor(urlEqualTo(endpoint))
                .withRequestBody(containing(TEST_CARDHOLDER)))

        and: "generated card number is returned"
        generatedCardNumber != null
        generatedCardNumber.length() == 16

        where:
        scheme                | provider     | endpoint                               | mockServer
        CardScheme.VISA       | "Visa"       | VisaCardProvider.VISA_GENERATE         | visaMock
        CardScheme.MASTERCARD | "Mastercard" | MasterCardProvider.MASTERCARD_GENERATE | mastercardMock
    }

    def "should handle CardCreationRequestedEvent for #scheme and emit CardRegistrationFailedEvent for connection problem with #provider provider"() {
        when: "CardCreationRequestedEvent with #scheme is published to Kafka"
        CardCreationRequestedEvent cardCreationRequestedEvent = cardCreationRequestEventIsPublished(scheme, "notMatchedCardHolder")

        then: "CardRegistrationFailedEvent was emitted"
        cardRegistrationFailedConsumer.verifyEventPublished(cardCreationRequestedEvent.prospectCardId)

        where:
        scheme                | provider
        CardScheme.VISA       | "Visa"
        CardScheme.MASTERCARD | "Mastercard"
    }

    private CardCreationRequestedEvent cardCreationRequestEventIsPublished(CardScheme scheme,
                                                                           String cardHolder = TEST_CARDHOLDER) {
        CardCreationRequestedEvent cardCreationRequestedEvent = new CardCreationRequestedEvent()
        cardCreationRequestedEvent.prospectCardId = UUID.randomUUID()
        cardCreationRequestedEvent.cardholderName = cardHolder
        cardCreationRequestedEvent.scheme = scheme
        cardCreationRequestedEvent.correlationId = UUID.randomUUID()
        cardCreationRequestedEvent.eventId = UUID.randomUUID()

        publishEvent(cardCreationRequestedEvent, CARD_CREATION_TOPIC)
        cardCreationRequestedEvent
    }

    WireMockServer createRunWiremock(int port) {
        WireMockServer wiremock = new WireMockServer(
                WireMockConfiguration.wireMockConfig().port(port)
        )
        wiremock.start()
        wiremock
    }

    private static String schemeReturnsCardData(WireMockServer server,
                                                String endpoint,
                                                String expectedCardholder) {
        String generatedCardNumber = ThreadLocalRandom.current()
                .nextLong(1_000_000_000_000_000L, 10_000_000_000_000_000L)
                .toString()

        server.stubFor(
                post(urlEqualTo(endpoint))
                        .withRequestBody(containing(expectedCardholder))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                        {
                            "cardNumber": "${generatedCardNumber}",
                            "cvv": "${DEFAULT_CVV}",
                            "expiryDate": "${DEFAULT_EXPIRY}"
                        }
                    """.stripIndent())
                        )
        )

        return generatedCardNumber
    }

}
