package core.framework.web.exception.support;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class ConstraintViolationExceptionHandler implements ExceptionHandler {
    @Override
    public Map<String, Object> handleHeaderAndMessage(HttpServletResponse response, Exception ex) {
        String errorMsg = ((ConstraintViolationException) ex).getConstraintViolations().stream().map(constraintViolation -> {
            return constraintViolation.getPropertyPath().toString() + constraintViolation.getMessage();
        }).collect(Collectors.joining(","));
        response.setHeader("error_code", "INTERNAL_SERVER_ERROR");
        return responseMessage(errorMsg, "INTERNAL_ERROR");
    }

    @Override
    public boolean support(Exception ex) {
        return ex instanceof ConstraintViolationException;
    }
}
