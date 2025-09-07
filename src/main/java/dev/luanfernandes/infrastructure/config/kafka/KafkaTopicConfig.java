package dev.luanfernandes.infrastructure.config.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic orderPaidTopic() {
        return TopicBuilder.name("order.paid")
                .partitions(3)
                .replicas(1) // Para desenvolvimento, em produção usar 2+
                .build();
    }

    @Bean
    public NewTopic stockUpdatedTopic() {
        return TopicBuilder.name("stock.updated").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderFailedDlqTopic() {
        return TopicBuilder.name("order.failed.dlq").partitions(1).replicas(1).build();
    }
}
