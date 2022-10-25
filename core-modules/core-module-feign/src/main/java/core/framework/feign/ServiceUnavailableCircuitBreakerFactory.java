package core.framework.feign;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.util.Assert;

import java.util.function.Function;

/**
 * @author ebin
 */
public class ServiceUnavailableCircuitBreakerFactory extends CircuitBreakerFactory<VoidConfigBuilder.Config, VoidConfigBuilder> {
    @Override
    public CircuitBreaker create(String id) {
        Assert.hasText(id, "A circuit breaker must have an id");
        return new ServiceUnavailableCircuitBreaker(id);
    }

    @Override
    protected VoidConfigBuilder configBuilder(String id) {
        return new VoidConfigBuilder();
    }

    @Override
    public void configureDefault(Function<String, VoidConfigBuilder.Config> defaultConfiguration) {
    }
}
