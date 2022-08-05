package core.framework.db.query;

import core.framework.db.exception.QueryNotFoundException;
import core.framework.utils.ResourcePatternResolverUtil;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.util.List;

public class MyBatisQueryParser implements InitializingBean, QueryParser {
    private final Logger logger = LoggerFactory.getLogger(MyBatisQueryParser.class);
    private Configuration configuration;

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
        List<Resource> resources = ResourcePatternResolverUtil.resolve("**/*Query.xml");
        for (Resource resource : resources) {
            if (resource == null)
                continue;
            try {
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource.getInputStream(), this.configuration,
                        resource.toString(), this.configuration.getSqlFragments());
                xmlMapperBuilder.parse();
                logger.info("Add query file. Filename = " + resource.getFilename());
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse mapping resource: '" + resource + "'");
            }
        }
    }
}
