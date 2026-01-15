package kderlatka.cardservice.providerintegration.provider.visa;

import com.fasterxml.jackson.annotation.JsonProperty;
import kderlatka.cardservice.providerintegration.ExternalCardData;
import kderlatka.cardservice.providerintegration.provider.SchemaCardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static kderlatka.cardservice.shared.card.CardScheme.VISA;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaCardResponse implements SchemaCardResponse {

    @JsonProperty("cardNumber")
    private String cardNumber;

    @JsonProperty("cvv")
    private String cvv;

    @JsonProperty("expiryDate")
    private String expiryDate;

    public ExternalCardData toExternalCardData() {
        return ExternalCardData.builder()
                .cardNumber(this.cardNumber)
                .cvv(this.cvv)
                .expiryDate(this.expiryDate)
                .cardType(VISA.name())
                .build();
    }
}
