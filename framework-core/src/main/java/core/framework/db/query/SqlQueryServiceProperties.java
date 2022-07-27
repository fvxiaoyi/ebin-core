package core.framework.db.query;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sql-query")
public class SqlQueryServiceProperties {
    private String[] mapperLocations;

    public String[] getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

}
