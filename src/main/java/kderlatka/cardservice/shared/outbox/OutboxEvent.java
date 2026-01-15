package kderlatka.cardservice.shared.outbox;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import kderlatka.cardservice.shared.event.domain.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox", indexes = {
        @Index(name = "idx_outbox_published", columnList = "published,created_at"),
        @Index(name = "idx_outbox_event_type", columnList = "event_type"),
        @Index(name = "idx_outbox_aggregate_id", columnList = "aggregate_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "aggregate_id", nullable = false, columnDefinition = "uuid")
    private UUID aggregateId;

    @Column(name = "correlation_id", columnDefinition = "uuid")
    private UUID correlationId;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Builder.Default
    @Column(name = "published", nullable = false)
    private boolean published = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public static OutboxEvent from(DomainEvent event) {
        try {
            String payload = new ObjectMapper().writeValueAsString(event);
            return OutboxEvent.builder()
                    .id(event.getEventId())
                    .eventType(event.getEventType())
                    .aggregateId(event.getProspectCardId())
                    .correlationId(event.getCorrelationId())
                    .payload(payload)
                    .published(false)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
