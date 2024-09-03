package br.com.microservices.orchestrated.productvalidationservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private static final int PARTITION_COUNT = 1;
    private static final int REPLICA_COUNT = 1;
    @Value("spring.kafka.bootstrap-servers")
    private String bootstrapServers;
    @Value("spring.kafka.group-id")
    private String groupId;
    @Value("spring.kafka.auto-offset-reset")
    private String autoOffsetReset;
    @Value("spring.kafka.topic.orchestrator")
    private String orchestrator;
    @Value("spring.kafka.topic.product-validation-success")
    private String productValidationSuccess;
    @Value("spring.kafka.topic.product-validation-fail")
    private String productValidationFail;

    @Bean
    public ConsumerFactory<String, String> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    private Map<String, Object> consumerProps(){
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerProps());
    }
    private Map<String, Object> producerProps() {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    private NewTopic buildTopic(String name){
        return TopicBuilder.name(name).replicas(REPLICA_COUNT).partitions(PARTITION_COUNT).build();
    }

    @Bean
    public NewTopic orchestratorTopic(){
        return buildTopic(orchestrator);
    }

    @Bean
    public NewTopic productSuccessTopic(){
        return buildTopic(productValidationSuccess);
    }

    @Bean
    public NewTopic productFailTopic(){
        return buildTopic(productValidationFail);
    }
}
