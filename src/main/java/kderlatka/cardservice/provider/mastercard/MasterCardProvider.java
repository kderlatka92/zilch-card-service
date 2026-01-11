package kderlatka.cardservice.provider.mastercard;

import kderlatka.cardservice.dto.CardCreateRequest;
import kderlatka.cardservice.dto.ExternalCardData;
import kderlatka.cardservice.provider.ExternalCardProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static kderlatka.cardservice.dto.CardCreateRequest.CardScheme.MASTERCARD;

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
        return mapCardDataOrThrowException(
                () -> callMasterCardForCard(request),
                () -> new MasterCardCardCreationException("MasterCard returned empty response"),
                e -> new MasterCardCardCreationException("Failed to generate MASTERCARD card: " + e.getMessage())
        );
    }

    private @Nullable MasterCardResponse callMasterCardForCard(CardCreateRequest request) {
        return restTemplate.postForObject(
                mastercardApiUrl + MASTERCARD_GENERATE,
                new MasterCardRequest(request.getCardholderName()),
                MasterCardResponse.class
        );
    }

    @Override
    public CardCreateRequest.CardScheme getSupportedSchema() {
        return MASTERCARD;
    }

}
