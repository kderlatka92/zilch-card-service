package kderlatka.provider;

import kderlatka.dto.CardCreateRequest;
import kderlatka.dto.ExternalCardData;

public interface ExternalCardProvider {

    ExternalCardData generateCard(CardCreateRequest request);

    CardCreateRequest.CardScheme getSupportedSchema();

    default boolean supports(CardCreateRequest.CardScheme scheme) {
        return scheme == getSupportedSchema();
    }

}
