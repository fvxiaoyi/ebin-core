package core.framework.utils.json;

import com.fasterxml.jackson.databind.JavaType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;

import static core.framework.utils.json.JSONMapper.OBJECT_MAPPER;


/**
 * @author ebin
 */
public final class JSON {
    private JSON() {
    }

    public static <T> T fromJSON(Type instanceType, String json) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructType(instanceType);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static <T> T fromJSON(Class<T> instanceClass, String json) {
        try {
            return OBJECT_MAPPER.readValue(json, instanceClass);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String toJSON(Object instance) {
        try {
            return OBJECT_MAPPER.writeValueAsString(instance);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
