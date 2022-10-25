package core.framework.web.exception.support;

import java.util.List;

/**
 * @author ebin
 */
@FunctionalInterface
public interface ExceptionHandlerCustomizer {
    List<ExceptionHandler> exceptionHandlers();
}
