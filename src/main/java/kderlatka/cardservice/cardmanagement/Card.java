package kderlatka.cardservice.cardmanagement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "card")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "card_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID cardId;

    @Column(name = "cardholder_name", nullable = false)
    private String cardholderName;

    @Column(name = "card_number", length = 19, unique = true)
    @Length(min = 15, max = 19, message = "Card number must be between 15 and 19 characters")
    private String cardNumber;

    @Column(name = "cvv", length = 4)
    @Length(min = 3, max = 4, message = "CVV must be between 3 and 4 characters")
    private String cvv;

    @Column(name = "expiry_date", length = 5)
    private String expiryDate;

    @Column(name = "card_type", length = 20)
    private String cardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CardStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}