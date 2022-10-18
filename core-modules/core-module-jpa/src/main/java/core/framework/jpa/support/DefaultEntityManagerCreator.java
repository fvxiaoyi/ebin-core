package core.framework.jpa.support;

/**
 * @author ebin
 */
public class DefaultEntityManagerCreator extends EntityManagerCreator {

    @Override
    protected Class<?>[] customizeInterfaces(Class<?>[] interfaces) {
        return interfaces;
    }
}
