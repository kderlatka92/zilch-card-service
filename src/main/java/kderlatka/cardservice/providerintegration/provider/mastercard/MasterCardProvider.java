package kderlatka.cardservice.providerintegration.provider.mastercard;

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

import static kderlatka.cardservice.shared.card.CardScheme.MASTERCARD;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterCardProvider implements ExternalCardProvider {

    public static final String MASTERCARD_GENERATE = "/mastercard/generate";

    private final RestTemplate restTemplate;

    @Value("${external.mastercard.url:http://localhost:8082}")
    private String mastercardApiUrl;

    @Override
    public ExternalCardData generateCard(CardCreationRequestedEvent event) {
        return mapCardDataOrThrowException(
                () -> callMasterCardForCard(event),
                () -> new MasterCardCardCreationException("MasterCard returned empty response"),
                e -> new MasterCardCardCreationException("Failed to generate MASTERCARD card: " + e.getMessage())
        );
    }

    private @Nullable MasterCardResponse callMasterCardForCard(CardCreationRequestedEvent event) {
        return restTemplate.postForObject(
                mastercardApiUrl + MASTERCARD_GENERATE,
                new MasterCardRequest(event.getCardholderName()),
                MasterCardResponse.class
        );
    }

    @Override
    public CardScheme getSupportedSchema() {
        return MASTERCARD;
    }

}
