package core.framework.web.exception.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
    private final Logger logger = LoggerFactory.getLogger(DefaultHandlerExceptionResolver.class);
    private final MappingJackson2JsonView jsonView;
    private final List<ExceptionHandler> exceptionHandlers = new ArrayList<>();
    private final DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

    public DefaultHandlerExceptionResolver() {
        this.jsonView = new MappingJackson2JsonView();
        this.jsonView.setExtractValueFromSingleKeyModel(true);
        addDefaultExceptionHandler();
    }

    public void addExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandlers.add(exceptionHandler);
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mv = new ModelAndView();

        ResponseStatus responseStatus = ex.getClass().getDeclaredAnnotation(ResponseStatus.class);
        if (responseStatus == null) {
            mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            mv.setStatus(responseStatus.value());
        }

        ExceptionHandler exceptionHandler = exceptionHandlers.stream()
                .filter(f -> f.support(ex)).findFirst()
                .orElse(defaultExceptionHandler);
        mv.addObject("exception", exceptionHandler.handleHeaderAndMessage(response, ex));
        mv.setView(jsonView);
        return mv;
    }

    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        logger.error(ex.getMessage(), ex);
    }

    private void addDefaultExceptionHandler() {
        this.exceptionHandlers.add(new BaseRuntimeExceptionHandler());
    }
}
