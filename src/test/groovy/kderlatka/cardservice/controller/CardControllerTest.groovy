package kderlatka.cardservice.controller

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.ThreadLocalRandom

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static kderlatka.cardservice.dto.CardCreateRequest.CardScheme.MASTERCARD
import static kderlatka.cardservice.dto.CardCreateRequest.CardScheme.VISA
import static kderlatka.cardservice.provider.mastercard.MasterCardProvider.MASTERCARD_GENERATE
import static kderlatka.cardservice.provider.visa.VisaCardProvider.VISA_GENERATE
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CardControllerTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Shared
    WireMockServer visaMock

    @Shared
    WireMockServer mastercardMock

    private static final String DEFAULT_CVV = "123"
    private static final String DEFAULT_EXPIRY = "12/25"
    private static final String TEST_CARDHOLDER = "Test Cardholder"

    private static final String CARDS_API_ENDPOINT = "/api/card"

    def setupSpec() {
        visaMock = createRunWiremock(8081)
        mastercardMock = createRunWiremock(8082)
    }

    def cleanupSpec() {
        visaMock.stop()
        mastercardMock.stop()
    }

    def "should successfully create #scheme card"() {
        given:
        String generatedCardNumber = schemeReturnsCardData(wireMockServer, endpoint, TEST_CARDHOLDER)
        String cardJson = buildCardJson(cardScheme: scheme.name())

        when:
        def result = postCardCreate(cardJson)

        then:
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.cardNumber').value(generatedCardNumber))
                .andExpect(jsonPath('$.cardholderName').value(TEST_CARDHOLDER))
                .andExpect(jsonPath('$.cvv').value(DEFAULT_CVV))
                .andExpect(jsonPath('$.expiryDate').value(DEFAULT_EXPIRY))
                .andExpect(jsonPath('$.cardType').value(scheme.name()))
                .andExpect(jsonPath('$.status').value('CREATED'))

        wireMockServer.verify(postRequestedFor(urlEqualTo(endpoint))
                .withRequestBody(containing(TEST_CARDHOLDER)))

        where:
        scheme     | endpoint            | wireMockServer
        VISA       | VISA_GENERATE       | visaMock
        MASTERCARD | MASTERCARD_GENERATE | mastercardMock
    }

    def "should fail to create card with unsupported scheme"() {
        given:
        String cardJson = buildCardJson(cardScheme: "NOT_SUPPORTED")

        when:
        def result = postCardCreate(cardJson)

        then:
        result.andExpect(status().isInternalServerError())
    }

    def "should fail to create card when external provider is down"() {
        given:
        def user = "Test User"
        visaReturns500ServiceUnavailable(user)

        String cardJson = buildCardJson(cardholderName: user)

        when:
        def result = postCardCreate(cardJson)

        then:
        result.andExpect(status().isInternalServerError())
    }

    def "should retrieve existing card by number"() {
        given:
        String cardNumber = schemeReturnsCardData(visaMock, VISA_GENERATE, TEST_CARDHOLDER)
        String cardJson = buildCardJson(cardScheme: VISA.name())

        and: "card is created"
        def createResult = postCardCreate(cardJson)
        createResult.andExpect(status().isCreated())

        when:
        def result = getCardByNumber(cardNumber)

        then:
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.cardNumber').value(cardNumber))
                .andExpect(jsonPath('$.cardholderName').value(TEST_CARDHOLDER))
                .andExpect(jsonPath('$.cvv').value(DEFAULT_CVV))
                .andExpect(jsonPath('$.expiryDate').value(DEFAULT_EXPIRY))
                .andExpect(jsonPath('$.cardType').value(VISA.name()))
                .andExpect(jsonPath('$.status').value('CREATED'))
                .andExpect(jsonPath('$.createdAt').isNotEmpty())
                .andExpect(jsonPath('$.updatedAt').isNotEmpty())
    }

    def "should return 404 when card does not exist"() {
        when:
        def result = getCardByNumber("nonExistentCardNumber")

        then:
        result.andExpect(status().isNotFound())
    }

    def "should retrieve card with special characters in cardholder name"() {
        given:
        String cardholderWithSpecialChars = "José García-López"
        String cardNumber = schemeReturnsCardData(visaMock, VISA_GENERATE, cardholderWithSpecialChars)
        String cardJson = buildCardJson(cardholderName: cardholderWithSpecialChars, cardScheme: VISA.name())

        def createResult = postCardCreate(cardJson)
        createResult.andExpect(status().isCreated())

        when:
        def getResult = getCardByNumber(cardNumber)

        then:
        getResult
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.cardNumber').value(cardNumber))
                .andExpect(jsonPath('$.cardholderName').value(cardholderWithSpecialChars))
    }

    def "should retrieve mastercard by number"() {
        given:
        String cardNumber = schemeReturnsCardData(mastercardMock, MASTERCARD_GENERATE, TEST_CARDHOLDER)
        String cardJson = buildCardJson(cardScheme: MASTERCARD.name())

        def createResult = postCardCreate(cardJson)
        createResult.andExpect(status().isCreated())

        when:
        def getResult = getCardByNumber(cardNumber)

        then:
        getResult
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.cardNumber').value(cardNumber))
                .andExpect(jsonPath('$.cardType').value(MASTERCARD.name()))
    }

    private StubMapping visaReturns500ServiceUnavailable(String user) {
        visaMock.stubFor(
                post(urlEqualTo(VISA_GENERATE))
                        .withRequestBody(containing(user))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withBody("Service Unavailable")
                        )
        )
    }

    private static String schemeReturnsCardData(WireMockServer server, String endpoint, String expectedCardholder) {
        String generatedCardNumber = ThreadLocalRandom.current().nextLong(1_000_000_000_000_000L, 10_000_000_000_000_000L).toString()

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

    private static String buildCardJson(Map overrides = [:]) {
        Map defaults = [
                cardScheme    : VISA.name(),
                cardholderName: TEST_CARDHOLDER,
                active        : true
        ]

        Map data = defaults + overrides

        return """
        {
            "card_scheme": "${data.cardScheme}",
            "cardholder_name": "${data.cardholderName}",
            "active": ${data.active}
        }
        """
    }

    private ResultActions postCardCreate(String cardJson) {
        mockMvc.perform(
                MockMvcRequestBuilders.post(CARDS_API_ENDPOINT)
                        .contentType("application/json")
                        .content(cardJson)
        )
    }

    private ResultActions getCardByNumber(String cardNumber) {
        mockMvc.perform(
                MockMvcRequestBuilders.get("$CARDS_API_ENDPOINT/$cardNumber")
                        .contentType("application/json")
        )
    }

    WireMockServer createRunWiremock(int port) {
        WireMockServer wiremock = new WireMockServer(WireMockConfiguration.wireMockConfig().port(port))
        wiremock.start()
        wiremock
    }
}
