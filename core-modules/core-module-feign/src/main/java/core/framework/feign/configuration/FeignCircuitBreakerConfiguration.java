package core.framework.feign.configuration;

import core.framework.feign.ServiceUnavailableCircuitBreakerFactory;
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
 * @see org.springframework.cloud.openfeign.FeignCircuitBreakerInvocationHandler
 */
@Configuration
public class FeignCircuitBreakerConfiguration {

    @Bean
    @ConditionalOnMissingBean(CircuitBreakerFactory.class)
    public CircuitBreakerFactory serviceUnavailableCircuitBreakerFactory() {
        return new ServiceUnavailableCircuitBreakerFactory();
    }
}
