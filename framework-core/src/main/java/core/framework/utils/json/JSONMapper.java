package core.framework.utils.json;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author ebin
 */
public final class JSONMapper {
    public static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private JSONMapper() {
    }

    private static ObjectMapper createObjectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .visibility(new VisibilityChecker.Std(ANY, ANY, ANY, ANY, ANY))
                .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .deactivateDefaultTyping()
                .build();
    }

}
