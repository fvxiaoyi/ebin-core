/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package core.framework.javaagent.model;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.trace.ReadableSpan;
import java.time.LocalDateTime;
import java.util.Objects;

public class Alert {
  public String app;
  public String action;
  public String errorCode;
  public String errorMessage;
  public String traceId;
  public String spanId;
  public LocalDateTime createdTime;

  public Alert() {}

  public Alert(String serviceName, ReadableSpan span) {
    this.app = serviceName;
    String httpMethod = span.getAttribute(AttributeKey.stringKey("http.method"));
    if (Objects.nonNull(httpMethod)) {
      this.action =
          String.format(
              "api:%s:%s", httpMethod, span.getAttribute(AttributeKey.stringKey("http.target")));
    }
    this.errorCode = span.getAttribute(AttributeKey.stringKey("error_code"));
    this.errorMessage = span.getAttribute(AttributeKey.stringKey("error_message"));
    this.traceId = span.getSpanContext().getTraceId();
    this.spanId = span.getSpanContext().getSpanId();
    this.createdTime = LocalDateTime.now();
  }

  @Override
  public String toString() {
    return "Alert{"
        + "app='"
        + app
        + '\''
        + ", action='"
        + action
        + '\''
        + ", errorCode='"
        + errorCode
        + '\''
        + ", errorMessage='"
        + errorMessage
        + '\''
        + ", traceId='"
        + traceId
        + '\''
        + ", spanId='"
        + spanId
        + '\''
        + ", createdTime="
        + createdTime
        + '}';
  }
}
