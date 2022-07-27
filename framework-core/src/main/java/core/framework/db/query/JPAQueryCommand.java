package core.framework.db.query;

/**
 * @author ebin
 */
public class JPAQueryCommand<T> extends AbstractQueryCommand<T> {
    private final String queryName;
    private final Class<T> resultType;

    public JPAQueryCommand(String queryName, Class<T> resultType) {
        this.queryName = queryName;
        this.resultType = resultType;
    }

    @Override
    public String getQueryName() {
        return queryName;
    }

    @Override
    public Class<T> getResultType() {
        return resultType;
    }
}
