package core.framework.query.impl;

import core.framework.query.PagingResult;
import core.framework.query.QueryCommand;
import core.framework.query.QueryParser;
import core.framework.query.QueryService;
import core.framework.query.QueryType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
public class RouterQueryService implements QueryService {
    protected final QueryParser queryParser;
    protected Map<QueryType, QueryService> queryServices = new HashMap<>();

    public void addQueryService(QueryType queryType, QueryService queryService) {
        queryServices.put(queryType, queryService);
    }

    public RouterQueryService(QueryParser queryParser) {
        this.queryParser = queryParser;
    }

    @Override
    public <T> List<T> select(QueryCommand<T> command) {
        return getQueryService(command).select(command);
    }

    @Override
    public <T> PagingResult<T> select(QueryCommand<T> command, int start, int limit) {
        return getQueryService(command).select(command, start, limit);
    }

    @Override
    public <T> Optional<T> get(QueryCommand<T> command) {
        return getQueryService(command).get(command);
    }

    private QueryService getQueryService(QueryCommand<?> command) {
        QueryType queryType = queryParser.getQueryType(command.getQueryName());
        return queryServices.get(queryType);
    }
}
