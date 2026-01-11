package kderlatka.cardservice.provider;

import kderlatka.cardservice.dto.CardCreateRequest;
import kderlatka.cardservice.dto.ExternalCardData;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ExternalCardProvider {

    ExternalCardData generateCard(CardCreateRequest request);

    CardCreateRequest.CardScheme getSupportedSchema();

    default boolean supports(CardCreateRequest.CardScheme scheme) {
        return scheme == getSupportedSchema();
    }

    default ExternalCardData mapCardDataOrThrowException(Supplier<SchemaCardResponse> httpCall,
                                                         Supplier<RuntimeException> emptyResponseExceptionProvider,
                                                         Function<RestClientException, RuntimeException> restClientExceptionHandler) {
        try {
            return Optional.ofNullable(httpCall.get())
                    .map(SchemaCardResponse::toExternalCardData)
                    .orElseThrow(emptyResponseExceptionProvider);
        } catch (RestClientException e) {
            throw restClientExceptionHandler.apply(e);
        }
    }

}
