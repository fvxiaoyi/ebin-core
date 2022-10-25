package core.framework.web.exception.support;

import core.framework.web.exception.BaseRuntimeException;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author ebin
 */
public class BaseRuntimeExceptionHandler implements ExceptionHandler {
    @Override
    public Map<String, Object> handleHeaderAndMessage(HttpServletResponse response, Exception ex) {
        BaseRuntimeException exception = (BaseRuntimeException) ex;
        response.setHeader("error_code", exception.errorCode());
        return responseMessage(ex.getMessage(), exception.errorCode());
    }

    @Override
    public boolean support(Exception ex) {
        return ex instanceof BaseRuntimeException;
    }
}
