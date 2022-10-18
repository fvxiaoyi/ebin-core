package core.framework.jpa.support;

import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.ResourceHolderSynchronization;

import javax.persistence.EntityManager;

/**
 * @author ebin
 */
public class ExtendedEntityManagerSynchronization extends ResourceHolderSynchronization<EntityManagerHolder, EntityManager> implements Ordered {

    private final EntityManager entityManager;

    @Nullable
    private final PersistenceExceptionTranslator exceptionTranslator;

    public volatile boolean closeOnCompletion;

    public ExtendedEntityManagerSynchronization(
            EntityManager em, @Nullable PersistenceExceptionTranslator exceptionTranslator) {

        super(new EntityManagerHolder(em), em);
        this.entityManager = em;
        this.exceptionTranslator = exceptionTranslator;
    }

    @Override
    public int getOrder() {
        return EntityManagerFactoryUtils.ENTITY_MANAGER_SYNCHRONIZATION_ORDER - 1;
    }

    @Override
    protected void flushResource(EntityManagerHolder resourceHolder) {
        try {
            this.entityManager.flush();
        } catch (RuntimeException ex) {
            throw convertException(ex);
        }
    }

    @Override
    protected boolean shouldReleaseBeforeCompletion() {
        return false;
    }

    @Override
    public void afterCommit() {
        super.afterCommit();
        // Trigger commit here to let exceptions propagate to the caller.
        try {
            this.entityManager.getTransaction().commit();
        } catch (RuntimeException ex) {
            throw convertException(ex);
        }
    }

    @Override
    public void afterCompletion(int status) {
        try {
            super.afterCompletion(status);
            if (status != STATUS_COMMITTED) {
                // Haven't had an afterCommit call: trigger a rollback.
                try {
                    this.entityManager.getTransaction().rollback();
                } catch (RuntimeException ex) {
                    throw convertException(ex);
                }
            }
        } finally {
            if (this.closeOnCompletion) {
                EntityManagerFactoryUtils.closeEntityManager(this.entityManager);
            }
        }
    }

    private RuntimeException convertException(RuntimeException ex) {
        DataAccessException dae = (this.exceptionTranslator != null) ?
                this.exceptionTranslator.translateExceptionIfPossible(ex) :
                EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
        return (dae != null ? dae : ex);
    }
}
