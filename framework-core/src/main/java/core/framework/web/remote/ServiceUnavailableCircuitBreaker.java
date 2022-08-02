package core.framework.web.remote;

import feign.FeignException;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author ebin
 */
public record ServiceUnavailableCircuitBreaker(String id) implements CircuitBreaker {

    @Override
    public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {
        try {
            return toRun.get();
        } catch (FeignException.ServiceUnavailable e) {
            return fallback.apply(e);
        }
    }
}
