package kderlatka.cardservice

import com.fasterxml.jackson.databind.ObjectMapper
import kderlatka.cardservice.shared.event.domain.CardCreationRequestedEvent
import kderlatka.cardservice.shared.event.domain.CardReadyForUseEvent
import kderlatka.cardservice.shared.event.domain.CardRegisteredWithProviderEvent
import kderlatka.cardservice.shared.event.domain.CardRegistrationFailedEvent
import kderlatka.cardservice.shared.event.domain.DomainEvent
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import spock.lang.Specification

import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

import static kderlatka.cardservice.shared.event.EventPublisher.CARD_CREATION_TOPIC
import static kderlatka.cardservice.shared.event.EventPublisher.CARD_READY_TOPIC
import static kderlatka.cardservice.shared.event.EventPublisher.CARD_REGISTERED_TOPIC
import static kderlatka.cardservice.shared.event.EventPublisher.CARD_REGISTRATION_FAILED_TOPIC

@SpringBootTest
@ActiveProfiles("test")
abstract class IntegrationSpec extends Specification {

    protected static final ConcurrentHashMap<String, SharedKafkaConsumer> sharedConsumers = new ConcurrentHashMap<>()
    static SharedKafkaConsumer<CardRegisteredWithProviderEvent> cardRegisteredWithProviderConsumer
    static SharedKafkaConsumer<CardRegistrationFailedEvent> cardRegistrationFailedConsumer
    static SharedKafkaConsumer<CardCreationRequestedEvent> cardCreationRequestedConsumer
    static SharedKafkaConsumer<CardReadyForUseEvent> cardReadyForUseConsumer

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate

    @Autowired
    ObjectMapper objectMapper

    @Container
    public static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
    )

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        kafka.start()
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers())
        cardRegisteredWithProviderConsumer = registerKafkaConsumer(CARD_REGISTERED_TOPIC, CardRegisteredWithProviderEvent)
        cardRegistrationFailedConsumer = registerKafkaConsumer(CARD_REGISTRATION_FAILED_TOPIC, CardRegistrationFailedEvent)
        cardCreationRequestedConsumer = registerKafkaConsumer(CARD_CREATION_TOPIC, CardCreationRequestedEvent)
        cardReadyForUseConsumer = registerKafkaConsumer(CARD_READY_TOPIC, CardReadyForUseEvent)
    }

    protected void publishEvent(Object event, String topic) {
        String eventJson = objectMapper.writeValueAsString(event)
        kafkaTemplate.send(topic, eventJson)
    }

     protected static <T extends DomainEvent> SharedKafkaConsumer<T> registerKafkaConsumer(String topic, Class<T> eventType) {
        def existing = sharedConsumers.get(topic)
        if (existing) {
            existing
        }
        Properties props = new Properties()
        props.put("bootstrap.servers", kafka.getBootstrapServers())
        props.put("group.id", "test-group-" + topic)
        props.put("auto.offset.reset", "earliest")
        props.put("key.deserializer", StringDeserializer.class.name)
        props.put("value.deserializer", StringDeserializer.class.name)

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)
        consumer.subscribe([topic])
        def wrapped = new SharedKafkaConsumer(eventType)
        wrapped.consumer = consumer
        sharedConsumers[topic] = wrapped
        wrapped
    }

    protected String consumeEventFromKafka(String topic,
                                           KafkaConsumer<String, String> consumer,
                                           int timeoutSeconds = 5) {
        try {
            def records = consumer.poll(Duration.ofSeconds(timeoutSeconds))
            assert !records.isEmpty(): "No message received from topic: $topic"

            def record = records.iterator().next()
            assert record.value() != null: "Message value is null from topic: $topic"

            return record.value()
        } finally {
            consumer.close()
        }
    }


}
