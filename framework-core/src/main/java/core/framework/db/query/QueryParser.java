package core.framework.db.query;

/**
 * @author ebin
 */
public interface QueryParser {
    String getQueryString(String queryName, Object params);

    String parseTotalQueryString(String queryString);
}
