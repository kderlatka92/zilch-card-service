package kderlatka.cardservice.shared.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kderlatka.cardservice.shared.card.CardScheme;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@ToString
public class CardCreationRequestedEvent extends DomainEvent {
    private String cardholderName;
    private CardScheme scheme;

    @JsonIgnore
    @Override
    public String getEventType() {
        return "CardCreationRequested";
    }
}
