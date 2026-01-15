package kderlatka.cardservice.cardcreation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import kderlatka.cardservice.shared.card.CardScheme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateRequest {

    @JsonProperty("card_scheme")
    private CardScheme cardScheme;

    @JsonProperty("cardholder_name")
    private String cardholderName;

    @JsonIgnore
    CardProspect toCardProspect() {
        UUID correlationId = UUID.randomUUID();

        return CardProspect.builder()
                .cardholderName(getCardholderName())
                .cardType(getCardScheme().name())
                .status(CardProspectStatus.REGISTERED)
                .correlationId(correlationId)
                .build();
    }

}
