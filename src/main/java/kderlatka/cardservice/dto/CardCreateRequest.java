package kderlatka.cardservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateRequest {

    @JsonProperty("card_scheme")
    private CardScheme cardScheme;

    @JsonProperty("cardholder_name")
    private String cardholderName;

    private Boolean active;

    public enum CardScheme {
        VISA, MASTERCARD
    }
}
