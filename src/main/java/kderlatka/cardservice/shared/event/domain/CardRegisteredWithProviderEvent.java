package kderlatka.cardservice.shared.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kderlatka.cardservice.shared.card.CardScheme;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CardRegisteredWithProviderEvent extends DomainEvent {
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    private String cardType;
    private String cardholderName;
    private CardScheme scheme;

    @JsonIgnore
    @Override
    public String getEventType() {
        return "CardRegisteredWithProvider";
    }
}
