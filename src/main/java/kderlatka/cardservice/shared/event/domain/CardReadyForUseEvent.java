package kderlatka.cardservice.shared.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kderlatka.cardservice.cardmanagement.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CardReadyForUseEvent extends DomainEvent {
    private UUID cardId;

    public static CardReadyForUseEvent of(CardRegisteredWithProviderEvent event,
                                          Card savedCard) {
        return CardReadyForUseEvent.builder()
                .prospectCardId(event.getProspectCardId())
                .correlationId(event.getCorrelationId())
                .cardId(savedCard.getCardId())
                .source("card-management-service")
                .build();
    }

    @JsonIgnore
    @Override
    public String getEventType() {
        return "CardReadyForUse";
    }
}
