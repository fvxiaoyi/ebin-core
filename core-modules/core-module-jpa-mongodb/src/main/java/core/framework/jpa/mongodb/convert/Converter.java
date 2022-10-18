package core.framework.jpa.mongodb.convert;

/**
 * @author ebin
 */
public interface Converter<C> {
    C convert(Object a);

    boolean support(Class<?> a, Class<?> b);
}
