package core.framework.alerting.application.listener;

import core.framework.alerting.application.service.BatchProcessAlertService;
import core.framework.alerting.domain.Alert;
import core.framework.alerting.domain.service.GetAlertMessageService;
import core.framework.json.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
@Service
public class OtlpSpanListener {
    @Autowired
    private GetAlertMessageService getAlertMessageService;

    @Autowired
    private BatchProcessAlertService batchSendAlertService;

    @KafkaListener(topics = {"otlp_spans"})
    public void handle(List<String> messages) {
        for (String message : messages) {
            Map<String, Object> map = JSON.fromJSON(Map.class, message);
            List<Map<String, Object>> resourceSpans = (List<Map<String, Object>>) map.get("resourceSpans");
            if (resourceSpans != null) {
                for (Map<String, Object> resourceSpan : resourceSpans) {
                    String serviceName = parseServiceName(resourceSpan);
                    List<Map<String, Object>> scopeSpans = (List<Map<String, Object>>) resourceSpan.get("scopeSpans");
                    if (serviceName != null && scopeSpans != null) {
                        parseScopeSpans(serviceName, scopeSpans);
                    }
                }
            }
        }
    }

    private String parseServiceName(Map<String, Object> resourceSpan) {
        if (resourceSpan == null) return null;
        Map<String, Object> resource = (Map<String, Object>) resourceSpan.get("resource");
        if (resource == null) return null;
        List<Map<String, Object>> attributes = (List<Map<String, Object>>) resource.get("attributes");
        for (Map<String, Object> attribute : attributes) {
            String key = (String) attribute.get("key");
            if (key.equals("service.name")) {
                Map<String, Object> value = (Map<String, Object>) attribute.get("value");
                return (String) value.get("stringValue");
            }
        }
        return null;
    }

    private void parseScopeSpans(String serviceName, List<Map<String, Object>> scopeSpans) {
        for (Map<String, Object> scopeSpan : scopeSpans) {
            List<Map<String, Object>> spans = (List<Map<String, Object>>) scopeSpan.get("spans");
            List<Alert> alerts = getAlerts(serviceName, spans);
            processAlert(alerts);
        }
    }

    private List<Alert> getAlerts(String serviceName, List<Map<String, Object>> spans) {
        if (spans == null) return List.of();
        List<Map<String, Object>> errorSpans = spans.stream().filter(span -> {
            String parentSpanId = (String) span.get("parentSpanId");
            Map<String, Object> status = (Map<String, Object>) span.get("status");
            return !StringUtils.hasText(parentSpanId) && "STATUS_CODE_ERROR".equals(status.get("code"));
        }).collect(Collectors.toList());

        return errorSpans.stream().map(span -> {
            String traceId = (String) span.get("traceId");
            String spanId = (String) span.get("spanId");
            String errorCode = null;
            String errorMessage = null;
            String httpMethod = null;
            String httpRequestUrl = null;
            List<Map<String, Object>> attributes = (List<Map<String, Object>>) span.get("attributes");
            for (Map<String, Object> attribute : attributes) {
                String key = (String) attribute.get("key");
                if (key.equals("error_code")) {
                    Map<String, Object> value = (Map<String, Object>) attribute.get("value");
                    errorCode = (String) value.get("stringValue");
                } else if (key.equals("http.method")) {
                    Map<String, Object> value = (Map<String, Object>) attribute.get("value");
                    httpMethod = (String) value.get("stringValue");
                } else if (key.equals("error_message")) {
                    Map<String, Object> value = (Map<String, Object>) attribute.get("value");
                    errorMessage = (String) value.get("stringValue");
                } else if (key.equals("http.target")) {
                    Map<String, Object> value = (Map<String, Object>) attribute.get("value");
                    httpRequestUrl = (String) value.get("stringValue");
                }
            }
            String action = httpMethod + ":" + httpRequestUrl;
            return new Alert(serviceName, action, errorCode, errorMessage, traceId, spanId);
        }).collect(Collectors.toList());
    }

    private void processAlert(List<Alert> alerts) {
        batchSendAlertService.process(alerts);
    }
}
