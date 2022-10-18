package core.framework.query;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public interface QueryService {
    <T> List<T> select(QueryCommand<T> command);

    <T> PagingResult<T> select(QueryCommand<T> command, int start, int limit);

    <T> Optional<T> get(QueryCommand<T> command);
}
