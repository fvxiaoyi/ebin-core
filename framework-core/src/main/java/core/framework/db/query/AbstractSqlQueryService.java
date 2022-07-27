package core.framework.db.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class AbstractSqlQueryService implements QueryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSqlQueryService.class);
    private final QueryParser queryParser;

    public AbstractSqlQueryService(QueryParser queryParser) {
        this.queryParser = queryParser;
    }

    @Override
    public <T> List<T> select(QueryCommand<T> command) {
        String sql = getSql(command.getQueryName(), command.getQueryParam());
        LOGGER.info(sql);
        return executeSelectQuery(sql, command.getResultType(), command.getQueryParam());
    }

    @Override
    public <T> PagingResult<T> select(QueryCommand<T> command, int start, int limit) {
        String sql = getSql(command.getQueryName(), command.getQueryParam());
        LOGGER.info(sql);
        String totalSql = queryParser.parseTotalQueryString(sql);
        LOGGER.info(totalSql);
        List<T> data = executePagingQuery(sql, command.getResultType(), command.getQueryParam(), start, limit);
        TotalQueryResult total = executeGetQuery(totalSql, TotalQueryResult.class, command.getQueryParam());
        return new PagingResult<T>(total.getTotal().longValue(), data);
    }

    @Override
    public <T> T get(QueryCommand<T> command) {
        String sql = getSql(command.getQueryName(), command.getQueryParam());
        LOGGER.info(sql);
        return executeGetQuery(sql, command.getResultType(), command.getQueryParam());
    }

    private String getSql(String queryName, Map<String, Object> param) {
        return queryParser.getQueryString(queryName, param);
    }

    protected abstract <T> List<T> executeSelectQuery(String sql, Class<T> beanClass, Map<String, Object> param);

    protected abstract <T> List<T> executePagingQuery(String sql, Class<T> beanClass, Map<String, Object> param, Integer start, Integer limit);

    protected abstract <T> T executeGetQuery(String sql, Class<T> beanClass, Map<String, Object> param);
}
