package core.framework.jpa.mongodb;

import org.hibernate.cfg.Environment;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.ogm.cfg.OgmProperties;
import org.hibernate.ogm.jpa.HibernateOgmPersistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class HibernateOGMPersistenceProvider extends HibernateOgmPersistence {

    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map) {
        Map<Object, Object> protectiveCopy = map != null ? new HashMap<Object, Object>(map) : new HashMap<Object, Object>();
        enforceOgmConfig(protectiveCopy);
        //HEM only builds an EntityManagerFactory when HibernatePersistence.class.getName() is the PersistenceProvider
        //that's why we override it when
        //new DelegatorPersistenceUnitInfo(info)
        return new HibernateOGMEntityManagerFactoryBuilderImpl(
                new PersistenceUnitInfoDescriptor(
                        info
                ), protectiveCopy, new ExtendClassLoaderServiceImpl()).build();
    }

    private void enforceOgmConfig(Map<Object, Object> map) {
        //we use a placeholder DS to make sure, Hibernate EntityManager (Ejb3Configuration) does not enforce a different connection provider
        map.put(Environment.DATASOURCE, "---PlaceHolderDSForOGM---");
        map.put(OgmProperties.ENABLED, true);
    }
}
