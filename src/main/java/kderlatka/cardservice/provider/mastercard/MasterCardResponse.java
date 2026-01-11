package kderlatka.cardservice.provider.mastercard;

import com.fasterxml.jackson.annotation.JsonProperty;
import kderlatka.cardservice.dto.ExternalCardData;
import kderlatka.cardservice.provider.SchemaCardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static kderlatka.cardservice.dto.CardCreateRequest.CardScheme.MASTERCARD;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterCardResponse implements SchemaCardResponse {

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
                .cardType(MASTERCARD.name())
                .build();
    }
}
