package core.framework.web.expand;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author ebin
 */
public class AnnotationLessRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {
    private final Validator validator;

    public AnnotationLessRequestMappingHandlerAdapter(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void afterPropertiesSet() {
        AnnotationLessHandlerMethodArgumentResolver annotationLessHandlerMethodArgumentResolver = new AnnotationLessHandlerMethodArgumentResolver(getMessageConverters(), validator);
        addCustomArgumentResolvers(annotationLessHandlerMethodArgumentResolver);
        super.afterPropertiesSet();
        List<HandlerMethodReturnValueHandler> handlerMethodReturnValueHandlers = new ArrayList<>(this.getReturnValueHandlers());
        int index = IntStream.range(0, handlerMethodReturnValueHandlers.size())
                .filter(f -> handlerMethodReturnValueHandlers.get(f) instanceof RequestResponseBodyMethodProcessor)
                .findFirst().orElse(-1);
        if (index > 0) {
            handlerMethodReturnValueHandlers.add(index, annotationLessHandlerMethodArgumentResolver);
        }
        setReturnValueHandlers(handlerMethodReturnValueHandlers);
    }

    protected void addCustomArgumentResolvers(HandlerMethodArgumentResolver resolver) {
        List<HandlerMethodArgumentResolver> customArgumentResolvers = getCustomArgumentResolvers();
        if (Objects.isNull(customArgumentResolvers)) {
            customArgumentResolvers = List.of(resolver);
            setCustomArgumentResolvers(customArgumentResolvers);
        } else {
            customArgumentResolvers.add(resolver);
            setCustomArgumentResolvers(customArgumentResolvers);
        }
    }
}
