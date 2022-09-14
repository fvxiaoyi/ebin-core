/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package core.framework.javaagent;

import com.google.auto.service.AutoService;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;

/**
 * @author ebin
 */
@AutoService(AutoConfigurationCustomizerProvider.class)
public class DefaultAutoConfigurationCustomizerProvider implements AutoConfigurationCustomizerProvider {

    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {
        autoConfiguration.addTracerProviderCustomizer(this::configureSdkTracerProvider);
    }

    private SdkTracerProviderBuilder configureSdkTracerProvider(
            SdkTracerProviderBuilder tracerProvider, ConfigProperties config) {
        return tracerProvider.addSpanProcessor(new SpanAlertingProcessor());
    }
}
