package core.framework.jpa.mongodb.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.jpa.mongodb")
public class HibernateMongoDBProperties {
    private String host;

    private String database;

    private String createDatabase;

    private String username;

    private String password;

    private String authenticationDatabase;

    private List<String> packagesToScan;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getCreateDatabase() {
        return Optional.ofNullable(createDatabase).orElse("true");
    }

    public void setCreateDatabase(String createDatabase) {
        this.createDatabase = createDatabase;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthenticationDatabase() {
        return authenticationDatabase;
    }

    public void setAuthenticationDatabase(String authenticationDatabase) {
        this.authenticationDatabase = authenticationDatabase;
    }

    public List<String> getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(List<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }
}
