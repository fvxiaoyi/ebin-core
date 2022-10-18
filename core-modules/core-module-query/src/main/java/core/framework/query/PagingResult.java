package core.framework.query;

import java.util.List;

/**
 * @author ebin
 */
public class PagingResult<T> {
    private final Long total;

    private final List<T> data;

    public PagingResult(Long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public List<T> getData() {
        return data;
    }
}
