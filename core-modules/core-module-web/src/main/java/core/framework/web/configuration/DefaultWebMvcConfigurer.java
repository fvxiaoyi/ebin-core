package core.framework.web.configuration;

import core.framework.web.exception.support.DefaultHandlerExceptionResolver;
import core.framework.web.exception.support.ExceptionHandlerCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

@Configuration
public class DefaultWebMvcConfigurer implements WebMvcConfigurer {
    private final ExceptionHandlerCustomizer exceptionHandlerCustomizer;

    public DefaultWebMvcConfigurer(ObjectProvider<ExceptionHandlerCustomizer> exceptionHandlerCustomizers) {
        this.exceptionHandlerCustomizer = exceptionHandlerCustomizers.getIfAvailable();
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new DefaultHandlerExceptionResolver(
                Optional.ofNullable(exceptionHandlerCustomizer)
                        .map(ExceptionHandlerCustomizer::exceptionHandlers).orElse(null)
        ));
    }
}
