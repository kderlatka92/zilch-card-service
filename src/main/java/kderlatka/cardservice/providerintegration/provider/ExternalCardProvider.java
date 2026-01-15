package kderlatka.cardservice.providerintegration.provider;

import kderlatka.cardservice.shared.event.domain.CardCreationRequestedEvent;
import kderlatka.cardservice.providerintegration.ExternalCardData;
import kderlatka.cardservice.shared.card.CardScheme;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ExternalCardProvider {

    ExternalCardData generateCard(CardCreationRequestedEvent event);

    CardScheme getSupportedSchema();

    default boolean supports(CardScheme scheme) {
        return scheme == getSupportedSchema();
    }

    default ExternalCardData mapCardDataOrThrowException(Supplier<SchemaCardResponse> httpCall,
                                                         Supplier<RuntimeException> emptyResponseExceptionProvider,
                                                         Function<RestClientException, RuntimeException> restClientExceptionHandler) {
        try {
            return Optional.ofNullable(httpCall.get())
                    .map(kderlatka.cardservice.providerintegration.provider.SchemaCardResponse::toExternalCardData)
                    .orElseThrow(emptyResponseExceptionProvider);
        } catch (RestClientException e) {
            throw restClientExceptionHandler.apply(e);
        }
    }

}
