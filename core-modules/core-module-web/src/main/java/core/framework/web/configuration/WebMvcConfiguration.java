package core.framework.web.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.framework.json.JSONMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import(DefaultWebMvcConfigurer.class)
public class WebMvcConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JSONMapper.OBJECT_MAPPER;
    }
}
