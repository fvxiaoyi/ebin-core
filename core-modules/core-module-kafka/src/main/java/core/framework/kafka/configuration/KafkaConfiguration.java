package core.framework.kafka.configuration;

import core.framework.kafka.publisher.MessagePublisher;
import core.framework.kafka.utils.Network;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
@Configuration
public class KafkaConfiguration {
    public static final int MAX_REQUEST_SIZE = 1024 * 1024;   // default 1M, refer to org.apache.kafka.clients.producer.ProducerConfig.MAX_REQUEST_SIZE_CONFIG

    public static final int MAX_POLL_RECORDS = 500;            // default kafka setting, refer to org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG
    public static final int MAX_POLL_BYTES = 3 * 1024 * 1024;  // get 3M bytes if possible for batching, this is not absolute limit of max bytes to poll, refer to org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_DOC
    public static final int MIN_POLL_BYTES = 1;                // default kafka setting
    public static final Duration MAX_WAIT_TIME = Duration.ofMillis(500);
    public static final Duration SESSION_TIMEOUT = Duration.ofSeconds(12);

    private final Environment environment;
    private final KafkaProperties properties;

    KafkaConfiguration(KafkaProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @Bean
    public DefaultKafkaConsumerFactoryCustomizer kafkaConsumerFactoryCustomizer() {
        return consumerFactory -> {
            Map<String, Object> config = new HashMap<>();
            config.put(CommonClientConfigs.CLIENT_ID_CONFIG, Network.LOCAL_HOST_NAME);

            config.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.application.name"));
            config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
            config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");                      // refer to org.apache.kafka.clients.consumer.ConsumerConfig, must be in("latest", "earliest", "none")
            config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1_800_000);                  // 30min as max process time for each poll
            config.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 500L);                       // longer backoff to reduce cpu usage when kafka is not available
            config.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 5_000L);                 // 5s
            config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_POLL_RECORDS);
            config.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, MAX_POLL_BYTES);
            config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, MIN_POLL_BYTES);
            config.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, (int) MAX_WAIT_TIME.toMillis());

            config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, (int) SESSION_TIMEOUT.toMillis());  // Locate dead consumers faster. Heartbeat.interval.ms default 3s and no higher than 1/3 of session.timeout.ms
            config.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
            consumerFactory.updateConfigs(config);
        };
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory
                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.properties.buildConsumerProperties())));
        factory.getContainerProperties().setSyncCommits(false);
        return factory;
    }

    @Bean
    public DefaultKafkaProducerFactoryCustomizer defaultKafkaProducerFactoryCustomizer() {
        return producerFactory -> {
            Map<String, Object> config = new HashMap<>();
            config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, CompressionType.SNAPPY.name); // if you uses JSON messages, you should use entropy-less encoders like Snappy and Lz4

            config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);                             // default 16kb
            config.put(ProducerConfig.LINGER_MS_CONFIG, 5L);
            config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, MAX_REQUEST_SIZE);

            config.put(ProducerConfig.ACKS_CONFIG, "all");
            config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

            config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 60_000);                   // 60s, Users should generally prefer to leave retries config unset and instead use delivery.timeout.ms to control retry behavior.
//            config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);           // default 5, A retry will be performed within delivery.timeout.ms. This causes messages to be out of order. If there is a strict order, please set it to 1.

            config.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 500L);                    // longer backoff to reduce cpu usage when kafka is not available
            config.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 5_000L);              // 5s
            config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30_000L);                         // 30s, metadata update timeout, shorter than default, to get exception sooner if kafka is not available

            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
            producerFactory.updateConfigs(config);
        };
    }

    @Bean
    public MessagePublisher messagePublisher() {
        return new MessagePublisher();
    }
}
