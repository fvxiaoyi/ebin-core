package core.framework.web.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.framework.json.JSONMapper;
import core.framework.web.mvc.RequestResponseBodyValidProcessorAdapter;
import core.framework.web.exception.support.BaseRuntimeExceptionHandler;
import core.framework.web.exception.support.BindExceptionHandler;
import core.framework.web.exception.support.ConstraintViolationExceptionHandler;
import core.framework.web.exception.support.ExceptionHandlerCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.validation.Validator;
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
                new ConstraintViolationExceptionHandler(),
                new BindExceptionHandler()
        );
    }

    @Bean
    public WebMvcRegistrations webMvcRegistrations(@Autowired Validator validator) {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
                return new RequestResponseBodyValidProcessorAdapter(validator);
            }
        };
    }
}
