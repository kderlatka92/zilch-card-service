package kderlatka.cardservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "card")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    static final String EMPTY = "empty";
    static final String MASKED_VALUE = "XXXX";

    @Id
    @Column(name = "card_number", nullable = false, length = 19)
    private String cardNumber;

    @Column(name = "cardholder_name", nullable = false)
    private String cardholderName;

    @Column(nullable = false, length = 4)
    private String cvv;

    @Column(name = "expiry_date", nullable = false, length = 5)
    private String expiryDate;

    @Column(name = "card_type", nullable = false, length = 20)
    private String cardType;

    @Column(nullable = false, length = 20)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return String.format(
                "Card{cardNumber='%s', cardholderName='%s', cvv=%s, expiryDate=%s, cardType='%s', status='%s', createdAt=%s, updatedAt=%s}",
                cardNumber,
                cardholderName,
                isNotBlank(cvv) ? MASKED_VALUE : EMPTY,
                isNotBlank(expiryDate) ? MASKED_VALUE : EMPTY,
                cardType,
                status,
                createdAt,
                updatedAt
        );
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
