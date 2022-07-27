package core.framework.db.exception;

import core.framework.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ebin
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class QueryNotFoundException extends BaseRuntimeException {
    public QueryNotFoundException(String queryName) {
        super("Query statment: [" + queryName + "] not found", HttpStatus.NOT_FOUND.toString());
    }
}
