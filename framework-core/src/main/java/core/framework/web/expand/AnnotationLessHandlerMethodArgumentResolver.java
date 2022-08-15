package core.framework.web.expand;

import core.framework.validate.ConstraintViolationException;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author ebin
 */
public class AnnotationLessHandlerMethodArgumentResolver extends RequestResponseBodyMethodProcessor {
    private final Validator validator;

    public AnnotationLessHandlerMethodArgumentResolver(List<HttpMessageConverter<?>> converters, Validator validator) {
        super(converters);
        this.validator = validator;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotations()
                && AnnotatedElementUtils.hasAnnotation(parameter.getContainingClass(), ResponseBody.class)) {
            Method method = parameter.getMethod();
            if (Objects.nonNull(method)) {
                return AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), PostMapping.class)
                        || AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), PutMapping.class)
                        || AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), PostMapping.class)
                        || AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), DeleteMapping.class);
            }
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        MethodParameter genParameter = parameter.nestedIfOptional();
        Object arg = readWithMessageConverters(webRequest, genParameter, genParameter.getNestedGenericParameterType());

        if (binderFactory != null && arg != null) {
            Set<ConstraintViolation<Object>> result = this.validator.validate(arg);
            if (!result.isEmpty()) {
                throw new ConstraintViolationException(result.stream().findFirst().get().getMessage());
            }
        }
        return adaptArgumentIfNecessary(arg, genParameter);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        if (returnValue != null && !returnType.hasMethodAnnotation(Valid.class)) {
            Set<ConstraintViolation<Object>> result = this.validator.validate(returnValue);
            if (!result.isEmpty()) {
                throw new ConstraintViolationException(result.stream().findFirst().get().getMessage());
            }
        }
        super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    @Override
    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        binder.validate();
    }
}
