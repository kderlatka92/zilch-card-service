package kderlatka.provider.visa;

import com.fasterxml.jackson.annotation.JsonProperty;
import kderlatka.dto.ExternalCardData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static kderlatka.dto.CardCreateRequest.CardScheme.VISA;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaCardResponse {

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
