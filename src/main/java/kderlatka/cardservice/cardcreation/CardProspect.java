package kderlatka.cardservice.cardcreation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "card_prospect")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardProspect {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "prospectId", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID prospectId;

    @Column(name = "cardId", columnDefinition = "uuid")
    private UUID cardId;

    @Column(name = "cardholder_name", nullable = false)
    private String cardholderName;

    @Column(name = "card_type", length = 20)
    private String cardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CardProspectStatus status;

    @Column(name = "correlation_id", columnDefinition = "uuid")
    private UUID correlationId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}