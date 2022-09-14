/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package core.framework.javaagent;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

import java.util.Objects;

/**
 * @author ebin
 */
public class SpanAlertingProcessor implements SpanProcessor {
    private final static String ERROR_CODE = "error_code";

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
    }

    @Override
    public boolean isStartRequired() {
        return false;
    }

    @Override
    public void onEnd(ReadableSpan span) {
        String errorCode = span.getAttribute(AttributeKey.stringKey(ERROR_CODE));
        if (Objects.nonNull(errorCode)) {
            //todo send alert to kafka
            /*Config config = Config.get();
            String serviceName = config.getString("otel.service.name");
            Alert alert = new Alert(serviceName, span);
            SlackClient.send(alert);*/
        }
    }

    @Override
    public boolean isEndRequired() {
        return true;
    }
}
