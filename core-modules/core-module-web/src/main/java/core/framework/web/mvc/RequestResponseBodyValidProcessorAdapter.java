package core.framework.web.mvc;

import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author ebin
 */
public class RequestResponseBodyValidProcessorAdapter extends RequestMappingHandlerAdapter {
    private final Validator validator;

    public RequestResponseBodyValidProcessorAdapter(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        List<HandlerMethodReturnValueHandler> handlerMethodReturnValueHandlers = new ArrayList<>(this.getReturnValueHandlers());
        int index = IntStream.range(0, handlerMethodReturnValueHandlers.size())
                .filter(f -> handlerMethodReturnValueHandlers.get(f) instanceof RequestResponseBodyMethodProcessor)
                .findFirst().orElse(-1);
        if (index > 0) {
            RequestResponseBodyValidProcessor requestResponseBodyValidProcessor = new RequestResponseBodyValidProcessor(getMessageConverters(), validator);
            handlerMethodReturnValueHandlers.add(index, requestResponseBodyValidProcessor);
        }
        setReturnValueHandlers(handlerMethodReturnValueHandlers);
    }
}
