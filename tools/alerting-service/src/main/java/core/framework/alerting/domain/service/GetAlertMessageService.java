package core.framework.alerting.domain.service;

import core.framework.alerting.domain.Alert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author ebin
 */
@Service
public class GetAlertMessageService implements InitializingBean {
    private final String linkTpl = "/explore?left={\\\"datasource\\\":\\\"Loki\\\",\\\"queries\\\":[{\\\"refId\\\":\\\"A\\\",\\\"expr\\\":\\\"{app=\\\\\\\"%s\\\\\\\", trace_id=\\\\\\\"%s\\\\\\\"}\\\"}],\\\"range\\\":{\\\"from\\\":\\\"now-1h\\\",\\\"to\\\":\\\"now\\\"}}";

    @Value("${lokiHost}")
    private String lokiHost;
    private String messageTemplate;

    public String get(Alert alert, int count) {
        String link = String.format(lokiHost + linkTpl, alert.getApp(), alert.getTraceId());
        return String.format(messageTemplate,
                alert.getApp(),
                alert.getAction(),
                alert.getErrorCode(),
                alert.getFormatCreatedTime(),
                alert.getErrorMessage(),
                count,
                link);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("alert-message-template.json");
        try (var inputStream = classPathResource.getInputStream()) {
            messageTemplate = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
