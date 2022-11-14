package core.framework.alerting.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ebin
 */
public class Alert {
    private String app;
    private String action;
    private String errorCode;
    private String errorMessage;
    private String traceId;
    private String spanId;
    private LocalDateTime createdTime;

    private Alert() {
    }

    public Alert(String app, String action, String errorCode, String errorMessage, String traceId, String spanId) {
        this.app = app;
        this.action = action;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.traceId = traceId;
        this.spanId = spanId;
        this.createdTime = LocalDateTime.now();
    }

    public String getAlertKey() {
        return this.getApp() + "/" + this.getAction() + "/" + this.getErrorCode();
    }

    public String getApp() {
        return app;
    }

    public String getAction() {
        return action;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public String getFormatCreatedTime() {
        return createdTime.format(DateTimeFormatter.ISO_DATE);
    }

    @Override
    public String toString() {
        return "Alert{" +
                "app='" + app + '\'' +
                ", action='" + action + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", traceId='" + traceId + '\'' +
                ", spanId='" + spanId + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}
