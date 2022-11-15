package core.framework.alerting.application.service;

import core.framework.alerting.domain.Alert;
import core.framework.alerting.domain.service.GetAlertMessageService;
import core.framework.utils.LRUCache;
import core.framework.utils.SlackClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ebin
 */
@Service
public class BatchProcessAlertService {
    private static final LRUCache<String, AlertStat> CACHE = new LRUCache<>(1000);
    private int timespanInMinutes;

    @Autowired
    private GetAlertMessageService getAlertMessageService;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Value("${slackUrl}")
    private String slackUrl;

    public void process(List<Alert> alerts) {
        for (Alert alert : alerts) {
            String key = alertKey(alert);
            Result result;
            synchronized (CACHE) {
                AlertStat stat = CACHE.get(key);
                if (stat == null) {
                    CACHE.put(key, new AlertStat(alert.getCreatedTime()));
                    result = new Result(true, 1);
                } else if (Duration.between(stat.lastSentDate, alert.getCreatedTime()).toMinutes() >= timespanInMinutes) {
                    CACHE.put(key, new AlertStat(alert.getCreatedTime()));
                    result = new Result(true, stat.alertCountSinceLastSent);
                } else {
                    stat.alertCountSinceLastSent++;
                    result = new Result(false, -1);
                }
            }
            if (result.notify) {
                doSend(alert, result.alertCountSinceLastSent);
            }
        }
    }

    private void doSend(Alert alert, int alertCountSinceLastSent) {
        taskExecutor.execute(() -> SlackClient.send(slackUrl, getAlertMessageService.get(alert, alertCountSinceLastSent)));
    }

    private String alertKey(Alert alert) {
        return alert.getApp() + "/" + alert.getAction() + "/" + alert.getErrorCode();
    }

    static class AlertStat {
        final LocalDateTime lastSentDate;
        int alertCountSinceLastSent;

        AlertStat(LocalDateTime lastSentDate) {
            this.lastSentDate = lastSentDate;
        }
    }

    static class Result {
        final boolean notify;
        final int alertCountSinceLastSent;

        Result(boolean notify, int alertCountSinceLastSent) {
            this.notify = notify;
            this.alertCountSinceLastSent = alertCountSinceLastSent;
        }
    }
}
