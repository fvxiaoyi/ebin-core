package core.framework.web.exception;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public interface ExceptionHandler {
    String ERROR_CODE = "error_code";
    String MESSAGE = "message";

    Map<String, Object> handleHeaderAndMessage(HttpServletResponse response, Exception ex);

    boolean support(Exception ex);

    default Map<String, Object> responseMessage(String message, String errorCode) {
        Map<String, Object> responseMessage = new HashMap<>(2);
        responseMessage.put(MESSAGE, message);
        responseMessage.put(ERROR_CODE, errorCode);
        return responseMessage;
    }
}
