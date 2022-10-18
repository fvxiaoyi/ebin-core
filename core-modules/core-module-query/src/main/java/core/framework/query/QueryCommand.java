package core.framework.query;

import java.util.Map;

/**
 * @author ebin
 */
public interface QueryCommand<T> {
    String getQueryName();

    Map<String, Object> getQueryParam();

    QueryCommand<T> addQueryParam(String key, Object value);

    Class<T> getResultType();
}
