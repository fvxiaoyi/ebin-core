package core.framework.web.expand;

import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;

/**
 * @author ebin
 */
public class RequestParamValidateRegistrator implements ApplicationListener<ContextRefreshedEvent> {
    private static final String VALID_PRE = "javax.validation";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        AnnotationLessRequestMappingHandlerAdapter annotationLessRequestMappingHandlerAdapter = applicationContext.getBean(AnnotationLessRequestMappingHandlerAdapter.class);
        AnnotationLessHandlerMethodArgumentResolver resolver = annotationLessRequestMappingHandlerAdapter.getAnnotationLessHandlerMethodArgumentResolver();
        if (Objects.isNull(resolver)) {
            return;
        }
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Collection<HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods().values();
        for (HandlerMethod handlerMethod : handlerMethods) {
            if (handlerMethod.getBeanType().equals(BasicErrorController.class)) {
                continue;
            }
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            handleMethodParameters(methodParameters, resolver);
            handleMethodReturnType(handlerMethod.getMethod().getReturnType(), resolver);
        }
    }

    private void handleMethodReturnType(Class<?> returnType, AnnotationLessHandlerMethodArgumentResolver resolver) {
        Field[] declaredFields = returnType.getDeclaredFields();
        for (Field field : declaredFields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation ann : annotations) {
                if (ann.annotationType().getName().startsWith(VALID_PRE)) {
                    resolver.addValidateRequestParamNames(returnType.getName());
                    return;
                }
            }
        }
    }

    private void handleMethodParameters(MethodParameter[] methodParameters, AnnotationLessHandlerMethodArgumentResolver resolver) {
        for (MethodParameter methodParameter : methodParameters) {
            if (handleMethodParameter(methodParameter, resolver)) {
                return;
            }
        }
    }

    private boolean handleMethodParameter(MethodParameter methodParameter, AnnotationLessHandlerMethodArgumentResolver resolver) {
        Field[] declaredFields = methodParameter.getParameterType().getDeclaredFields();
        for (Field field : declaredFields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation ann : annotations) {
                if (ann.annotationType().getName().startsWith(VALID_PRE)) {
                    resolver.addValidateRequestParamNames(methodParameter.getParameterType().getName());
                    return true;
                }
            }
        }
        return false;
    }
}
