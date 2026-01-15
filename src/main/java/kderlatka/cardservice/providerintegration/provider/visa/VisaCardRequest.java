package kderlatka.cardservice.providerintegration.provider.visa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class VisaCardRequest {
    private String holderName;
}
