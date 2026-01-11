package kderlatka.cardservice.provider.visa;

import kderlatka.cardservice.dto.CardCreateRequest;
import kderlatka.cardservice.dto.ExternalCardData;
import kderlatka.cardservice.provider.ExternalCardProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static kderlatka.cardservice.dto.CardCreateRequest.CardScheme.VISA;

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
        return mapCardDataOrThrowException(
                () -> callVisaForCard(request),
                () -> new VisaCardCreationException("Visa returned empty response"),
                e -> new VisaCardCreationException("Failed to generate VISA card: " + e.getMessage())
        );
    }

    private @Nullable VisaCardResponse callVisaForCard(CardCreateRequest request) {
        return restTemplate.postForObject(
                visaApiUrl + VISA_GENERATE,
                new VisaCardRequest(request.getCardholderName()),
                VisaCardResponse.class
        );
    }

    @Override
    public CardCreateRequest.CardScheme getSupportedSchema() {
        return VISA;
    }

}
