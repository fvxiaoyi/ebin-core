package apps.config;

/**
 * @author ebin
 */
public interface PersistenceUnitCustomizer {
    String persistenceUnitName();

    String[] packagesToScan();
}
