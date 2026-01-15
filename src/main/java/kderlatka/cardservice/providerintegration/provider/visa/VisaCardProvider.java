package kderlatka.cardservice.providerintegration.provider.visa;

import kderlatka.cardservice.shared.event.domain.CardCreationRequestedEvent;
import kderlatka.cardservice.providerintegration.ExternalCardData;
import kderlatka.cardservice.providerintegration.provider.ExternalCardProvider;
import kderlatka.cardservice.shared.card.CardScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static kderlatka.cardservice.shared.card.CardScheme.VISA;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisaCardProvider implements ExternalCardProvider {

    public static final String VISA_GENERATE = "/visa/generate";
    private final RestTemplate restTemplate;

    @Value("${external.visa.url:http://localhost:8081}")
    private String visaApiUrl;


    @Override
    public ExternalCardData generateCard(CardCreationRequestedEvent event) {
        return mapCardDataOrThrowException(
                () -> callVisaForCard(event),
                () -> new VisaCardCreationException("Visa returned empty response"),
                e -> new VisaCardCreationException("Failed to generate VISA card: " + e.getMessage())
        );
    }

    private @Nullable VisaCardResponse callVisaForCard(CardCreationRequestedEvent event) {
        return restTemplate.postForObject(
                visaApiUrl + VISA_GENERATE,
                new VisaCardRequest(event.getCardholderName()),
                VisaCardResponse.class
        );
    }

    @Override
    public CardScheme getSupportedSchema() {
        return VISA;
    }

}
