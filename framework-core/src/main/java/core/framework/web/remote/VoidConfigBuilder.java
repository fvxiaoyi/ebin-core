package core.framework.web.remote;

import org.springframework.cloud.client.circuitbreaker.ConfigBuilder;

/**
 * @author ebin
 */
public class VoidConfigBuilder implements ConfigBuilder<VoidConfigBuilder.Config> {
    @Override
    public Config build() {
        return new Config();
    }

    public static class Config {
    }
}
