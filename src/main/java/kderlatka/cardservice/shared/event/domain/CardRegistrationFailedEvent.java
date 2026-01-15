package kderlatka.cardservice.shared.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CardRegistrationFailedEvent extends DomainEvent {
    private String reason;
    private String errorCode;

    @JsonIgnore
    @Override
    public String getEventType() {
        return "CardRegistrationFailed";
    }
}
