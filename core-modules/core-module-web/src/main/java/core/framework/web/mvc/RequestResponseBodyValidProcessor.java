package core.framework.web.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * @author ebin
 */
public class RequestResponseBodyValidProcessor extends RequestResponseBodyMethodProcessor {
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private final Validator validator;

    public RequestResponseBodyValidProcessor(List<HttpMessageConverter<?>> converters, Validator validator) {
        super(converters);
        this.validator = validator;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        if (returnValue != null && !returnType.hasMethodAnnotation(Valid.class)) {
            Set<ConstraintViolation<Object>> result = this.validator.validate(returnValue);
            if (!result.isEmpty()) {
                throw new ConstraintViolationException(result);
            }
        }
        super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    @Override
    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation ann : annotations) {
            Object[] validationHints = determineValidationHints(ann);
            if (validationHints != null) {
                binder.validate(validationHints);
                break;
            }
        }
    }

    private Object[] determineValidationHints(Annotation ann) {
        Class<? extends Annotation> annotationType = ann.annotationType();
        if (RequestBody.class == annotationType) {
            return EMPTY_OBJECT_ARRAY;
        }
        return null;
    }
}
