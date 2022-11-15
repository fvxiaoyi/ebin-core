package core.framework.jpa.support;

/**
 * @author ebin
 */
public class DefaultEntityManagerCreator extends AbstractEntityManagerCreator {

    @Override
    protected Class<?>[] customizeInterfaces(Class<?>[] interfaces) {
        return interfaces;
    }
}
