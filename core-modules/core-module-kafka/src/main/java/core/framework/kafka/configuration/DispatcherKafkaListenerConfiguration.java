package core.framework.kafka.configuration;

import core.framework.kafka.consumer.ExtendKafkaListenerConfigurationSelector;
import core.framework.kafka.utils.Network;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

/**
 * @author ebin
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.kafka.dispatcher", name = "enable")
@Import(ExtendKafkaListenerConfigurationSelector.class)
@EnableConfigurationProperties({DispatcherKafkaListenerProperties.class})
public class DispatcherKafkaListenerConfiguration {
    public static final String DISPATCHER_KAFKA_LISTENER_CONTAINER_FACTORY_NAME = "dispatcherKafkaListenerContainerFactory";

    private final KafkaProperties properties;

    DispatcherKafkaListenerConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }

    @Bean(name = DISPATCHER_KAFKA_LISTENER_CONTAINER_FACTORY_NAME)
    ConcurrentKafkaListenerContainerFactory<?, ?> dispatcherKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory
                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.properties.buildConsumerProperties())));
        factory.getContainerProperties().setSyncCommits(false);
        factory.getContainerProperties().setClientId(Network.LOCAL_HOST_NAME + "-dispatched");
        return factory;
    }
}
