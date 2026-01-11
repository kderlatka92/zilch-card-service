package kderlatka.provider.mastercard;

import kderlatka.dto.CardCreateRequest;
import kderlatka.dto.ExternalCardData;
import kderlatka.provider.ExternalCardProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static kderlatka.dto.CardCreateRequest.CardScheme.MASTERCARD;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterCardProvider implements ExternalCardProvider {

    public static final String MASTERCARD_GENERATE = "/mastercard/generate";

    private final RestTemplate restTemplate;

    @Value("${external.mastercard.url:http://localhost:8082}")
    private String mastercardApiUrl;

    @Override
    public ExternalCardData generateCard(CardCreateRequest request) {
        try {
            MasterCardResponse response = restTemplate.postForObject(
                    mastercardApiUrl + MASTERCARD_GENERATE,
                    new MasterCardRequest(request.getCardholderName()),
                    MasterCardResponse.class
            );

            return response.toExternalCardData();
        } catch (RestClientException e) {
            log.error("Error calling MasterCard API", e);
            throw new RuntimeException("Failed to generate MASTERCARD card: " + e.getMessage());
        }
    }

    @Override
    public CardCreateRequest.CardScheme getSupportedSchema() {
        return MASTERCARD;
    }

}
