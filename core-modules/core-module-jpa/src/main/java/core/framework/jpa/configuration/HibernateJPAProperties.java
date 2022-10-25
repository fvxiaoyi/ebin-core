package core.framework.jpa.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.jpa.common")
public class HibernateJPAProperties {
    private String basePackagePath;

    public String getBasePackagePath() {
        return basePackagePath;
    }

    public void setBasePackagePath(String basePackagePath) {
        this.basePackagePath = basePackagePath;
    }

}
