package core.framework.kafka.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.kafka.dispatched")
public class DispatchedKafkaListenerProperties {
    private List<String> topics = new ArrayList<>();
}
