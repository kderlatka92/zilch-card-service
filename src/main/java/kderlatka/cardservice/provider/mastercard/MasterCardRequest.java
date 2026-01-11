package kderlatka.cardservice.provider.mastercard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterCardRequest {
    private String holderName;
}
