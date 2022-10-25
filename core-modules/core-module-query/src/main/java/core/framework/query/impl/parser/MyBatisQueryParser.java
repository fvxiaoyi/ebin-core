package core.framework.query.impl.parser;

import core.framework.query.QueryParser;
import core.framework.query.QueryType;
import core.framework.query.utils.ResourcePatternResolverUtil;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class MyBatisQueryParser implements InitializingBean, QueryParser {
    private final Logger logger = LoggerFactory.getLogger(MyBatisQueryParser.class);
    private final static Pattern SELECT_SQL_PATTERN = Pattern.compile(".*(select|SELECT).*(from|FROM)\\s+.*(where|WHERE)?.*");
    private Map<String, QueryType> queryTypes;
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
            throw new RuntimeException("Query statment: [" + queryName + "] not found");
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
    public QueryType getQueryType(String queryName) {
        QueryType queryType = queryTypes.get(queryName);
        if (queryType == null) {
            throw new RuntimeException("Query statment: [" + queryName + "] not found");
        }
        return queryType;
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
        Map<String, QueryType> queryTypes = new HashMap<>();
        List<String> queryNames = this.configuration.getMappedStatementNames().stream().filter(f -> f.contains(".")).collect(Collectors.toList());
        queryNames.forEach(queryName -> {
            MappedStatement statement = this.configuration.getMappedStatement(queryName);
            BoundSql boundSql = statement.getBoundSql(Collections.emptyMap());
            queryTypes.put(queryName,
                    SELECT_SQL_PATTERN.matcher(boundSql.getSql()).matches()
                            ? QueryType.SQL
                            : QueryType.NOSQL);
        });

        this.queryTypes = Collections.unmodifiableMap(queryTypes);
    }
}
