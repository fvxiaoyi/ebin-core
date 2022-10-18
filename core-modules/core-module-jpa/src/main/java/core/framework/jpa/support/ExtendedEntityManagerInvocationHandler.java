package core.framework.jpa.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TransactionRequiredException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ebin
 */
public class ExtendedEntityManagerInvocationHandler implements InvocationHandler, Serializable {

    private static final Log logger = LogFactory.getLog(ExtendedEntityManagerInvocationHandler.class);

    private final EntityManager target;

    @Nullable
    private final PersistenceExceptionTranslator exceptionTranslator;

    private final boolean jta;

    private final boolean containerManaged;

    private final boolean synchronizedWithTransaction;

    public ExtendedEntityManagerInvocationHandler(EntityManager target,
                                                   @Nullable PersistenceExceptionTranslator exceptionTranslator, @Nullable Boolean jta,
                                                   boolean containerManaged, boolean synchronizedWithTransaction) {

        this.target = target;
        this.exceptionTranslator = exceptionTranslator;
        this.jta = (jta != null ? jta : isJtaEntityManager());
        this.containerManaged = containerManaged;
        this.synchronizedWithTransaction = synchronizedWithTransaction;
    }

    private boolean isJtaEntityManager() {
        try {
            this.target.getTransaction();
            return false;
        } catch (IllegalStateException ex) {
            logger.debug("Cannot access EntityTransaction handle - assuming we're in a JTA environment");
            return true;
        }
    }

    @Override
    @Nullable
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Invocation on EntityManager interface coming in...

        switch (method.getName()) {
            case "equals":
                // Only consider equal when proxies are identical.
                return (proxy == args[0]);
            case "hashCode":
                // Use hashCode of EntityManager proxy.
                return hashCode();
            case "getTargetEntityManager":
                // Handle EntityManagerProxy interface.
                return this.target;
            case "unwrap":
                // Handle JPA 2.0 unwrap method - could be a proxy match.
                Class<?> targetClass = (Class<?>) args[0];
                if (targetClass == null) {
                    return this.target;
                } else if (targetClass.isInstance(proxy)) {
                    return proxy;
                }
                break;
            case "isOpen":
                if (this.containerManaged) {
                    return true;
                }
                break;
            case "close":
                if (this.containerManaged) {
                    throw new IllegalStateException("Invalid usage: Cannot close a container-managed EntityManager");
                }
                ExtendedEntityManagerSynchronization synch = (ExtendedEntityManagerSynchronization)
                        TransactionSynchronizationManager.getResource(this.target);
                if (synch != null) {
                    // Local transaction joined - don't actually call close() before transaction completion
                    synch.closeOnCompletion = true;
                    return null;
                }
                break;
            case "getTransaction":
                if (this.synchronizedWithTransaction) {
                    throw new IllegalStateException(
                            "Cannot obtain local EntityTransaction from a transaction-synchronized EntityManager");
                }
                break;
            case "joinTransaction":
                doJoinTransaction(true);
                return null;
            case "isJoinedToTransaction":
                // Handle JPA 2.1 isJoinedToTransaction method for the non-JTA case.
                if (!this.jta) {
                    return TransactionSynchronizationManager.hasResource(this.target);
                }
                break;
        }

        // Do automatic joining if required. Excludes toString, equals, hashCode calls.
        if (this.synchronizedWithTransaction && method.getDeclaringClass().isInterface()) {
            doJoinTransaction(false);
        }

        // Invoke method on current EntityManager.
        try {
            return method.invoke(this.target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    /**
     * Join an existing transaction, if not already joined.
     *
     * @param enforce whether to enforce the transaction
     *                (i.e. whether failure to join is considered fatal)
     */
    private void doJoinTransaction(boolean enforce) {
        if (this.jta) {
            // Let's try whether we're in a JTA transaction.
            try {
                this.target.joinTransaction();
                logger.debug("Joined JTA transaction");
            } catch (TransactionRequiredException ex) {
                if (!enforce) {
                    logger.debug("No JTA transaction to join: " + ex);
                } else {
                    throw ex;
                }
            }
        } else {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                if (!TransactionSynchronizationManager.hasResource(this.target) &&
                        !this.target.getTransaction().isActive()) {
                    enlistInCurrentTransaction();
                }
                logger.debug("Joined local transaction");
            } else {
                if (!enforce) {
                    logger.debug("No local transaction to join");
                } else {
                    throw new TransactionRequiredException("No local transaction to join");
                }
            }
        }
    }

    /**
     * Enlist this application-managed EntityManager in the current transaction.
     */
    private void enlistInCurrentTransaction() {
        // Resource local transaction, need to acquire the EntityTransaction,
        // start a transaction now and enlist a synchronization for commit or rollback later.
        EntityTransaction et = this.target.getTransaction();
        et.begin();
        if (logger.isDebugEnabled()) {
            logger.debug("Starting resource-local transaction on application-managed " +
                    "EntityManager [" + this.target + "]");
        }
        ExtendedEntityManagerSynchronization extendedEntityManagerSynchronization =
                new ExtendedEntityManagerSynchronization(this.target, this.exceptionTranslator);
        TransactionSynchronizationManager.bindResource(this.target, extendedEntityManagerSynchronization);
        TransactionSynchronizationManager.registerSynchronization(extendedEntityManagerSynchronization);
    }
}
