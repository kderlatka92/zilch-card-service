package kderlatka.cardservice.providerintegration.provider.mastercard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class MasterCardRequest {
    private String holderName;
}
