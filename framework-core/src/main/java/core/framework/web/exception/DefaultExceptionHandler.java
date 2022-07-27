package core.framework.web.exception;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author ebin
 */
public class DefaultExceptionHandler implements ExceptionHandler {
    @Override
    public Map<String, Object> handleHeaderAndMessage(HttpServletResponse response, Exception ex) {
        response.setHeader("error_code", "INTERNAL_SERVER_ERROR");
        return responseMessage(ex.getMessage(), "INTERNAL_ERROR");
    }

    @Override
    public boolean support(Exception ex) {
        return true;
    }
}
