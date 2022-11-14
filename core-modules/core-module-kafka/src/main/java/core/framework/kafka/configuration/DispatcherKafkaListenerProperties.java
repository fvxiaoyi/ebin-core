package core.framework.kafka.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.kafka.dispatcher")
public class DispatcherKafkaListenerProperties {
    private boolean enable;
    private List<String> topics;
    private Integer concurrency = 1;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Integer getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Integer concurrency) {
        this.concurrency = concurrency;
    }
}
