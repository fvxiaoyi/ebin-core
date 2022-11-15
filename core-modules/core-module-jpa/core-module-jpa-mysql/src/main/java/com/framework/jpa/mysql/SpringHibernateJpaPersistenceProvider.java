package com.framework.jpa.mysql;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class SpringHibernateJpaPersistenceProvider extends HibernatePersistenceProvider {

    @Override
    @SuppressWarnings("rawtypes")
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
        final List<String> mergedClassesAndPackages = new ArrayList<>(info.getManagedClassNames());
        if (info instanceof SmartPersistenceUnitInfo) {
            mergedClassesAndPackages.addAll(((SmartPersistenceUnitInfo) info).getManagedPackages());
        }
        return new EntityManagerFactoryBuilderImpl(
                new PersistenceUnitInfoDescriptor(info) {
                    @Override
                    public List<String> getManagedClassNames() {
                        return mergedClassesAndPackages;
                    }
                }, properties).build();
    }

}
