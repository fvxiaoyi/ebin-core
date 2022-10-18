package core.framework.alerting.application.listener;

import core.framework.alerting.domain.Alert;
import core.framework.alerting.domain.service.GetAlertMessageService;
import core.framework.utils.json.JSON;
import core.framework.utils.slack.SlackClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
@Service
public class OtlpSpanListener {
    @Autowired
    GetAlertMessageService getAlertMessageService;

    @KafkaListener(topics = {"otlp_spans"})
    public void handle(String data) {
        Map<String, Object> map = JSON.fromJSON(Map.class, data);
        List<Map<String, Object>> resourceSpans = (List<Map<String, Object>>) map.get("resourceSpans");
        if (resourceSpans != null) {
            for (Map<String, Object> resourceSpan : resourceSpans) {
                String serviceName = null;
                Map<String, Object> resource = (Map<String, Object>) resourceSpan.get("resource");
                List<Map<String, Object>> attributes = (List<Map<String, Object>>) resource.get("attributes");
                for (Map<String, Object> attribute : attributes) {
                    String key = (String) attribute.get("key");
                    if (key.equals("service.name")) {
                        Map<String, Object> value = (Map<String, Object>) attribute.get("value");
                        serviceName = (String) value.get("stringValue");
                    }
                }
                List<Map<String, Object>> scopeSpans = (List<Map<String, Object>>) resourceSpan.get("scopeSpans");
                if (serviceName != null && scopeSpans != null) {
                    parseScopeSpans(serviceName, scopeSpans);
                }
            }
        }
    }

    private void parseScopeSpans(String serviceName, List<Map<String, Object>> scopeSpans) {
        for (Map<String, Object> scopeSpan : scopeSpans) {
            List<Map<String, Object>> spans = (List<Map<String, Object>>) scopeSpan.get("spans");
            if (spans != null) {
                for (Map<String, Object> span : spans) {
                    Map<String, Object> status = (Map<String, Object>) span.get("status");
                    if ("STATUS_CODE_ERROR".equals(status.get("code"))) {
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
                        Alert alert = new Alert(serviceName, action, errorCode, errorMessage, traceId, spanId);
                        SlackClient.send(getAlertMessageService.get(alert));
                    }
                }
            }
        }
    }
}
