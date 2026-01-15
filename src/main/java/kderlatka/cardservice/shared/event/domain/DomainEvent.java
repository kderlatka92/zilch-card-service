package kderlatka.cardservice.shared.event.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public abstract class DomainEvent {
    private UUID eventId;
    private UUID prospectCardId;
    private UUID correlationId;
    private LocalDateTime timestamp;
    private String source;

    public abstract String getEventType();
}
