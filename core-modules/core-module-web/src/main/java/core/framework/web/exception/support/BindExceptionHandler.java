package core.framework.web.exception.support;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class BindExceptionHandler implements ExceptionHandler {
    @Override
    public Map<String, Object> handleHeaderAndMessage(HttpServletResponse response, Exception ex) {
        StringBuilder errorMsg = new StringBuilder();
        Map<String, List<FieldError>> errorMap = ((BindException) ex).getFieldErrors().stream().collect(Collectors.groupingBy(k -> k.getField()));
        errorMap.forEach((filed, errors) -> errorMsg
                .append(filed)
                .append(
                        errors.stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.joining(","))
                )
                .append(";"));
        response.setHeader("error_code", "INTERNAL_SERVER_ERROR");
        return responseMessage(errorMsg.toString(), "INTERNAL_ERROR");
    }

    @Override
    public boolean support(Exception ex) {
        return ex instanceof BindException;
    }
}
