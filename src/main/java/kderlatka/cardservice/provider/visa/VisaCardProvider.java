package kderlatka.provider.visa;

import kderlatka.dto.CardCreateRequest;
import kderlatka.dto.ExternalCardData;
import kderlatka.provider.ExternalCardProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static kderlatka.dto.CardCreateRequest.CardScheme.VISA;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisaCardProvider implements ExternalCardProvider {

    public static final String VISA_GENERATE = "/visa/generate";
    private final RestTemplate restTemplate;

    @Value("${external.visa.url:http://localhost:8081}")
    private String visaApiUrl;

    @Override
    public ExternalCardData generateCard(CardCreateRequest request) {
        try {
            VisaCardResponse response = restTemplate.postForObject(
                    visaApiUrl + VISA_GENERATE,
                    new VisaCardRequest(request.getCardholderName()),
                    VisaCardResponse.class
            );

            return response.toExternalCardData();
        } catch (RestClientException e) {
            log.error("Error calling Visa API", e);
            throw new RuntimeException("Failed to generate VISA card: " + e.getMessage());
        }
    }

    @Override
    public CardCreateRequest.CardScheme getSupportedSchema() {
        return VISA;
    }

}
