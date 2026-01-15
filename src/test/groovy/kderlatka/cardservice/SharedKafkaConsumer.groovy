package kderlatka.cardservice

import kderlatka.cardservice.shared.event.domain.DomainEvent
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.StreamSupport

import static org.awaitility.Awaitility.await

class SharedKafkaConsumer<R extends DomainEvent> {

    private static final Duration MAX_WAIT_TIME = Duration.ofSeconds(5)
    private static final Duration POLL_INTERVAL = Duration.ofMillis(100)

    ObjectMapper objectMapper = new ObjectMapper()
    ConcurrentHashMap<UUID, R> events = new ConcurrentHashMap<>()

    KafkaConsumer<String, String> consumer
    Class<R> eventClass

    SharedKafkaConsumer(Class<R> eventClass) {
        this.eventClass = eventClass
    }

    R verifyEventPublished(UUID prospectCardId) {
        await()
                .atMost(MAX_WAIT_TIME)
                .pollInterval(POLL_INTERVAL)
                .untilAsserted {
                    StreamSupport.stream(consumer.poll(Duration.ofSeconds(90)).spliterator(), false)
                            .map { record -> record.value() }
                            .map { recordValue -> objectMapper.readValue(recordValue, eventClass) }
                            .forEach { event -> events.put(event.prospectCardId, event) }
                    assert events.containsKey(prospectCardId)
                }
        events.get(prospectCardId)
    }

}
