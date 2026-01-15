package kderlatka.cardservice.cardcreation

import kderlatka.cardservice.IntegrationSpec
import kderlatka.cardservice.shared.card.CardScheme
import kderlatka.cardservice.shared.event.domain.CardCreationRequestedEvent
import kderlatka.cardservice.shared.event.domain.CardReadyForUseEvent
import kderlatka.cardservice.shared.event.domain.CardRegistrationFailedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

import java.time.Duration

import static org.awaitility.Awaitility.await
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
class CardCreationIntegrationSpec extends IntegrationSpec {

    @Autowired
    MockMvc mockMvc

    @Autowired
    CardProspectRepository cardProspectRepository

    private static final String VISA_CARD_TYPE = CardScheme.VISA.name()
    private static final Duration MAX_WAIT_TIME = Duration.ofSeconds(5)
    private static final Duration POLL_INTERVAL = Duration.ofMillis(100)

    def "should create card prospect via REST endpoint and publish CardCreationRequestedEvent"() {
        given: "a valid card creation request"
        String uniqueName = "TestCardholder-${System.nanoTime()}"
        CardCreateRequest request = new CardCreateRequest(
                cardholderName: uniqueName,
                cardScheme: CardScheme.VISA
        )

        when: "POST request is sent to /api/card endpoint"
        def response = mockMvc.perform(
                post("/api/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )

        then: "should return 202 ACCEPTED status"
        response.andExpect(status().isAccepted())

        and: "response should contain prospect details"
        response
                .andExpect(jsonPath('$.prospectId').exists())
                .andExpect(jsonPath('$.cardholderName').value(uniqueName))
                .andExpect(jsonPath('$.status').value("REGISTERED"))

        and: "prospect should be saved in database with correct data"
        def savedProspect = cardProspectRepository.findAll()
                .stream()
                .filter { it.cardholderName == uniqueName }
                .findFirst()
                .get()

        savedProspect.prospectId != null
        savedProspect.cardholderName == uniqueName
        savedProspect.status == CardProspectStatus.REGISTERED
        savedProspect.cardType == "VISA"

        and: "CardCreationRequestedEvent should be published to Kafka"
        def cardCreationRequestedEvent = cardCreationRequestedConsumer.verifyEventPublished(savedProspect.prospectId)
        cardCreationRequestedEvent.cardholderName == uniqueName
        cardCreationRequestedEvent.scheme == CardScheme.VISA
    }

    def "should handle CardRegistrationFailedEvent and update prospect status to FAILED"() {
        given: "a prospect manually created in database with status REGISTERED"
        String uniqueName = "EventTest-${System.nanoTime()}"
        CardProspect savedProspect = createProspectInDatabase(uniqueName)
        UUID prospectId = savedProspect.prospectId

        assertProspectHasStatus(prospectId, CardProspectStatus.REGISTERED)

        when: "CardRegistrationFailedEvent is published to Kafka"
        CardRegistrationFailedEvent failedEvent = new CardRegistrationFailedEvent()
        failedEvent.prospectCardId = prospectId
        failedEvent.reason = "Invalid card data"

        publishEvent(failedEvent, "card-registration-failed")

        then: "prospect status should be updated to FAILED in database"
        waitForProspectStatus(prospectId, CardProspectStatus.FAILED)
    }

    def "should handle CardReadyForUseEvent and update prospect status to CREATED"() {
        given: "a prospect manually created in database with status REGISTERED"
        String uniqueName = "ReadyEventTest-${System.nanoTime()}"
        CardProspect savedProspect = createProspectInDatabase(uniqueName)
        UUID prospectId = savedProspect.prospectId

        assertProspectHasStatus(prospectId, CardProspectStatus.REGISTERED)

        when: "CardReadyForUseEvent is published to Kafka"
        UUID assignedCardId = UUID.randomUUID()
        CardReadyForUseEvent readyEvent = new CardReadyForUseEvent()
        readyEvent.prospectCardId = prospectId
        readyEvent.cardId = assignedCardId

        publishEvent(readyEvent, "card-ready")

        then: "prospect status should be updated to CREATED and cardId should be set"
        waitForProspectStatus(prospectId, CardProspectStatus.CREATED) { prospect ->
            assert prospect.cardId != null
            assert prospect.cardId == assignedCardId
        }
    }

    private CardProspect createProspectInDatabase(
            String cardholderName,
            String cardType = VISA_CARD_TYPE,
            CardProspectStatus status = CardProspectStatus.REGISTERED) {

        CardProspect prospect = new CardProspect()
        prospect.cardholderName = cardholderName
        prospect.cardType = cardType
        prospect.status = status
        prospect.correlationId = UUID.randomUUID()

        return cardProspectRepository.save(prospect)
    }

    private void waitForProspectStatus(UUID prospectId, CardProspectStatus expectedStatus, Closure closure = null) {
        await()
                .atMost(MAX_WAIT_TIME)
                .pollInterval(POLL_INTERVAL)
                .untilAsserted {
                    def prospect = cardProspectRepository.findById(prospectId).get()
                    assert prospect.status == expectedStatus
                    closure?.call(prospect)
                }
    }

    private void assertProspectHasStatus(UUID prospectId, CardProspectStatus expectedStatus) {
        def prospect = cardProspectRepository.findById(prospectId).get()
        assert prospect != null
        assert prospect.status == expectedStatus
    }

}
