package core.framework.kafka.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
@Configuration
public class KafkaConfiguration {
    public int maxPollRecords = 500;            // default kafka setting, refer to org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG
    public int maxPollBytes = 3 * 1024 * 1024;  // get 3M bytes if possible for batching, this is not absolute limit of max bytes to poll, refer to org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_DOC
    public int minPollBytes = 1;                // default kafka setting
    public Duration maxWaitTime = Duration.ofMillis(500);

    @Autowired
    Environment environment;

    @Bean
    public DefaultKafkaConsumerFactoryCustomizer kafkaConsumerFactoryCustomizer() {
        return consumerFactory -> {
            Map<String, Object> config = new HashMap<>();
            config.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.application.name"));
            config.put(ConsumerConfig.CLIENT_ID_CONFIG, "");
            config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
            config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");                      // refer to org.apache.kafka.clients.consumer.ConsumerConfig, must be in("latest", "earliest", "none")
            config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1_800_000);                  // 30min as max process time for each poll
            config.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 500L);                       // longer backoff to reduce cpu usage when kafka is not available
            config.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 5_000L);                 // 5s
            config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
            config.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPollBytes);
            config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, minPollBytes);
            config.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, (int) maxWaitTime.toMillis());
            consumerFactory.updateConfigs(config);
        };
    }
}
