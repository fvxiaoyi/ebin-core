package core.framework.query;

import java.math.BigInteger;

/**
 * @author ebin
 */
public class TotalQueryResult {
    public static final TotalQueryResult EMPTY = new TotalQueryResult(BigInteger.ZERO);

    private BigInteger total;

    public TotalQueryResult() {
    }

    public TotalQueryResult(BigInteger total) {
        this.total = total;
    }

    public BigInteger getTotal() {
        return total;
    }

    public void setTotal(BigInteger total) {
        this.total = total;
    }
}
