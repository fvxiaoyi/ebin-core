package core.framework.db.query;

import java.util.List;

/**
 * @author ebin
 */
public interface QueryService {
    <T> List<T> select(QueryCommand<T> command);

    <T> PagingResult<T> select(QueryCommand<T> command, int start, int limit);

    <T> T get(QueryCommand<T> command);
}
