package core.framework.configuration;

import core.framework.web.remote.ServiceUnavailableCircuitBreakerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Call feign fallback method when http response status is SERVICE_UNAVAILABLE(503).
 *
 * use:
 * feign.circuitbreaker.enabled=true
 *
 * @author ebin
 */
@Configuration
public class CircuitBreakerConfig {

    @Bean
    @ConditionalOnMissingBean(CircuitBreakerFactory.class)
    public CircuitBreakerFactory serviceUnavailableCircuitBreakerFactory() {
        return new ServiceUnavailableCircuitBreakerFactory();
    }
}
