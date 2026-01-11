package kderlatka.dto;

import kderlatka.domain.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalCardData {

    public static final String CREATED_STATUS = "CREATED";
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    private String cardType;

    public Card asCreatedCard(CardCreateRequest request) {
        return Card.builder()
                .cardNumber(getCardNumber())
                .cardholderName(request.getCardholderName())
                .cvv(getCvv())
                .expiryDate(getExpiryDate())
                .cardType(getCardType())
                .status(CREATED_STATUS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
