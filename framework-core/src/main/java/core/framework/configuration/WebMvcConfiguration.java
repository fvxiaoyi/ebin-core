package core.framework.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.framework.utils.json.JSONMapper;
import core.framework.web.expand.AnnotationLessRequestMappingHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.validation.Validator;

@Configuration
public class WebMvcConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JSONMapper.OBJECT_MAPPER;
    }

    @Bean
    public WebMvcRegistrations webMvcRegistrations(@Autowired Validator validator) {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
                return new AnnotationLessRequestMappingHandlerAdapter(validator);
            }
        };
    }

}
