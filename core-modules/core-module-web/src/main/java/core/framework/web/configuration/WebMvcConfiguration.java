package core.framework.web.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.framework.json.JSONMapper;
import core.framework.web.exception.support.BaseRuntimeExceptionHandler;
import core.framework.web.exception.support.ConstraintViolationExceptionHandler;
import core.framework.web.exception.support.ExceptionHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
@Import(DefaultWebMvcConfigurer.class)
public class WebMvcConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JSONMapper.OBJECT_MAPPER;
    }

    @Bean
    public ExceptionHandlerCustomizer exceptionHandlerCustomizer() {
        return () -> List.of(
                new BaseRuntimeExceptionHandler(),
                new ConstraintViolationExceptionHandler()
        );
    }
}
