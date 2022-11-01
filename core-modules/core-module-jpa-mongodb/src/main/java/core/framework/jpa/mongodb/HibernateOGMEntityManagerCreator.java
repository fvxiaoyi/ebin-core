package core.framework.jpa.mongodb;

import core.framework.jpa.support.AbstractEntityManagerCreator;
import org.hibernate.ogm.OgmSession;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class HibernateOGMEntityManagerCreator extends AbstractEntityManagerCreator {

    @Override
    protected Class<?>[] customizeInterfaces(Class<?>[] interfaces) {
        List<Class<?>> collect = Arrays.stream(interfaces).filter(inf -> inf == OgmSession.class).collect(Collectors.toList());
        return collect.toArray(new Class<?>[]{});
    }
}
