package kderlatka.cardservice.providerintegration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalCardData {

    private String cardNumber;
    private String cvv;
    private String expiryDate;
    private String cardType;

}
