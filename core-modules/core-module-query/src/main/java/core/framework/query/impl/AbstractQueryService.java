package core.framework.query.impl;

import core.framework.query.PagingResult;
import core.framework.query.QueryCommand;
import core.framework.query.QueryParser;
import core.framework.query.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
public abstract class AbstractQueryService implements QueryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQueryService.class);
    protected final QueryParser queryParser;

    public AbstractQueryService(QueryParser queryParser) {
        this.queryParser = queryParser;
    }

    @Override
    public <T> List<T> select(QueryCommand<T> command) {
        String queryStatement = getQueryStatement(command.getQueryName(), command.getQueryParam());
        LOGGER.info(queryStatement);
        return executeSelectQuery(queryStatement, command.getResultType(), command.getQueryParam());
    }

    @Override
    public <T> PagingResult<T> select(QueryCommand<T> command, int start, int limit) {
        String queryStatement = getQueryStatement(command.getQueryName(), command.getQueryParam());
        LOGGER.info(queryStatement);
        String totalQueryStatement = getTotalQueryStatement(command.getQueryName(), command.getQueryParam());
        LOGGER.info(totalQueryStatement);
        List<T> data = executePagingQuery(queryStatement, command.getResultType(), command.getQueryParam(), start, limit);
        Long total = executeTotalQuery(totalQueryStatement, command.getQueryParam());
        return new PagingResult<T>(total, data);
    }

    @Override
    public <T> Optional<T> get(QueryCommand<T> command) {
        String queryStatement = getQueryStatement(command.getQueryName(), command.getQueryParam());
        LOGGER.info(queryStatement);
        return executeGetQuery(queryStatement, command.getResultType(), command.getQueryParam());
    }

    protected abstract String getQueryStatement(String queryName, Map<String, Object> param);

    protected abstract String getTotalQueryStatement(String queryName, Map<String, Object> param);

    protected abstract <T> List<T> executeSelectQuery(String sql, Class<T> beanClass, Map<String, Object> param);

    protected abstract <T> List<T> executePagingQuery(String sql, Class<T> beanClass, Map<String, Object> param, Integer start, Integer limit);

    protected abstract <T> Optional<T> executeGetQuery(String sql, Class<T> beanClass, Map<String, Object> param);

    protected abstract Long executeTotalQuery(String sql, Map<String, Object> param);
}
