package core.framework.jpa.configuration;

import java.util.List;

/**
 * @author ebin
 */
public class HibernateJPAProperties {
    private String basePackagePath;
    private List<String> packagesToScan;

    public String getBasePackagePath() {
        return basePackagePath;
    }

    public void setBasePackagePath(String basePackagePath) {
        this.basePackagePath = basePackagePath;
    }

    public List<String> getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(List<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }
}
