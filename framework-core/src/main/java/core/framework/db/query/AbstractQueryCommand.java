package core.framework.db.query;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public abstract class AbstractQueryCommand<T> implements QueryCommand<T> {
    private final Map<String, Object> queryParams = new HashMap<>();

    @Override
    public Map<String, Object> getQueryParam() {
        return Collections.unmodifiableMap(queryParams);
    }

    @Override
    public QueryCommand<T> addQueryParam(String key, Object value) {
        queryParams.put(key, value);
        return this;
    }
}
