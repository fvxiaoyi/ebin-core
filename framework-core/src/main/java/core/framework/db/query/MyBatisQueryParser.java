package core.framework.db.query;

import core.framework.db.exception.QueryNotFoundException;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class MyBatisQueryParser implements InitializingBean, QueryParser {
    private final SqlQueryServiceProperties properties;
    private Configuration configuration;

    public MyBatisQueryParser(SqlQueryServiceProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getQueryString(String queryName, Object params) {
        MappedStatement statement = this.configuration.getMappedStatement(queryName);
        if (statement != null) {
            try {
                BoundSql boudSql = statement.getBoundSql(params);
                return boudSql.getSql();
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new QueryNotFoundException(queryName);
        }
    }

    @Override
    public String parseTotalQueryString(String queryString) {
        String totalQueryPre = "SELECT count(*) as total ";
        int fromIndex = queryString.indexOf("FROM");
        if (fromIndex == -1) {
            fromIndex = queryString.indexOf("from");
        }
        String result = totalQueryPre + queryString.substring(fromIndex);
        result = result.replaceAll("(?i)\\sorder(\\s)+by.+", " ");
        return result;
    }

    @Override
    public void afterPropertiesSet() {
        this.configuration = new Configuration();
        Resource[] mapperLocations = resolveMapperLocations(this.properties.getMapperLocations());
        for (Resource mapperLocation : mapperLocations) {
            if (mapperLocation == null)
                continue;
            try {
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(), this.configuration,
                        mapperLocation.toString(), this.configuration.getSqlFragments());
                xmlMapperBuilder.parse();
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse mapping resource: '" + mapperLocation + "'");
            }
        }
    }
}
