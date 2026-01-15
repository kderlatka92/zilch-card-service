package kderlatka.cardservice.shared.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByPublishedFalseOrderByCreatedAtAsc();
    long countByPublishedFalse();
    List<OutboxEvent> findByEventType(String eventType);

    Optional<OutboxEvent> findByAggregateId(UUID uuid);
}
