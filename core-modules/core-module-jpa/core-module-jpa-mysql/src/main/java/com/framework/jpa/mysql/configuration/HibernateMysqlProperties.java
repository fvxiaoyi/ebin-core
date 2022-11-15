package com.framework.jpa.mysql.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.jpa.mysql")
public class HibernateMysqlProperties {
    private List<String> packagesToScan;

    public List<String> getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(List<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }
}
