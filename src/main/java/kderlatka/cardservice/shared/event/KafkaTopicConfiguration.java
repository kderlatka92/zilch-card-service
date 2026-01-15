package kderlatka.cardservice.shared.event;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic cardCreationRequestedTopic() {
        return TopicBuilder.name("card-creation-requested")
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000")  // 7 days
                .config("compression.type", "snappy")
                .build();
    }

    @Bean
    public NewTopic cardRegisteredTopic() {
        return TopicBuilder.name("card-registered")
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000")
                .config("compression.type", "snappy")
                .build();
    }

    @Bean
    public NewTopic cardRegistrationFailedTopic() {
        return TopicBuilder.name("card-registration-failed")
                .partitions(1)
                .replicas(1)
                .config("retention.ms", "2592000000")  // 30 days
                .build();
    }

    @Bean
    public NewTopic cardReadyTopic() {
        return TopicBuilder.name("card-ready")
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000")
                .build();
    }
}
