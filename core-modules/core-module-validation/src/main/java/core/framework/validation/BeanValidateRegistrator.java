package core.framework.validation;

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

/**
 * @author ebin
 */
public class BeanValidateRegistrator implements ApplicationListener<ContextRefreshedEvent> {
    private static final String VALID_PRE = "javax.validation";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        processController(applicationContext);

    }

    private void processController(ApplicationContext applicationContext) {
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Collection<HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods().values();
        for (HandlerMethod handlerMethod : handlerMethods) {
            if (handlerMethod.getBeanType().equals(BasicErrorController.class)) {
                continue;
            }
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            handleMethodParameters(methodParameters);
            handleMethodReturnType(handlerMethod.getMethod().getReturnType());
        }
    }

    private void handleMethodReturnType(Class<?> returnType) {
        Field[] declaredFields = returnType.getDeclaredFields();
        for (Field field : declaredFields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation ann : annotations) {
                if (ann.annotationType().getName().startsWith(VALID_PRE)) {
                    BeanValidatorManager.addValidator(returnType);
                    return;
                }
            }
        }
    }

    private void handleMethodParameters(MethodParameter[] methodParameters) {
        for (MethodParameter methodParameter : methodParameters) {
            if (handleMethodParameter(methodParameter)) {
                return;
            }
        }
    }

    private boolean handleMethodParameter(MethodParameter methodParameter) {
        Field[] declaredFields = methodParameter.getParameterType().getDeclaredFields();
        for (Field field : declaredFields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation ann : annotations) {
                if (ann.annotationType().getName().startsWith(VALID_PRE)) {
                    BeanValidatorManager.addValidator(methodParameter.getParameterType());
                    return true;
                }
            }
        }
        return false;
    }
}
