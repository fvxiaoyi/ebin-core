package core.framework.feign;

import feign.FeignException;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author ebin
 */
public class ServiceUnavailableCircuitBreaker implements CircuitBreaker {
    private String id;

    public ServiceUnavailableCircuitBreaker(String id) {
        this.id = id;
    }

    @Override
    public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {
        try {
            return toRun.get();
        } catch (FeignException.ServiceUnavailable e) {
            return fallback.apply(e);
        }
    }
}
