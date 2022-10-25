package core.framework.web.configuration;

import core.framework.web.exception.support.DefaultHandlerExceptionResolver;
import core.framework.web.exception.support.ExceptionHandlerCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class DefaultWebMvcConfigurer implements WebMvcConfigurer {
    private final ObjectProvider<ExceptionHandlerCustomizer> exceptionHandlerCustomizers;

    public DefaultWebMvcConfigurer(ObjectProvider<ExceptionHandlerCustomizer> exceptionHandlerCustomizers) {
        this.exceptionHandlerCustomizers = exceptionHandlerCustomizers;
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        DefaultHandlerExceptionResolver defaultHandlerExceptionResolver = new DefaultHandlerExceptionResolver();
        exceptionHandlerCustomizers.orderedStream().forEach(exceptionHandlerCustomizer -> {
            exceptionHandlerCustomizer.exceptionHandlers().forEach(defaultHandlerExceptionResolver::addExceptionHandler);
        });
        resolvers.add(defaultHandlerExceptionResolver);
    }
}
