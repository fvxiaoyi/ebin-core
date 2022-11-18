package apps;

import com.framework.jpa.mysql.impl.MysqlDomainEventTracking;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy;
import org.hibernate.tool.schema.TargetType;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

/**
 * @author ebin
 */
public class DomainEntitySqlGenerator {
    public static void main(String... strings) {
        List<Class<?>> managerClasses = List.of(MysqlDomainEventTracking.class);
        stdoutUpdateSchema(managerClasses);
    }

    public static void stdoutUpdateSchema(List<Class<?>> managerClasses) {
        EntityManagerFactoryBuilderImpl builder = genEntityManagerFactoryBuilder(managerClasses);
        builder.build();
        new SchemaUpdate().setFormat(true).execute(EnumSet.of(TargetType.STDOUT), builder.getMetadata());
    }

    private static EntityManagerFactoryBuilderImpl genEntityManagerFactoryBuilder(List<Class<?>> managerClasses) {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new ClassPathResource("application.yaml"));
        Properties properties = factoryBean.getObject();
        List<String> pkgs = new ArrayList<>();
        int i = 0;
        String pkg;
        do {
            pkg = (String) properties.get("spring.jpa.mysql.packagesToScan[" + i + "]");
            if (pkg != null) {
                pkgs.add(pkg);
            }
            i++;
        } while (pkg != null);
        SpringApplication application = new SpringApplication(DataSourceAutoConfiguration.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setBannerMode(Banner.Mode.OFF);
        ApplicationContext applicationContext = application.run();
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        Properties props = new Properties();
        props.put(Environment.DIALECT, MySQL57Dialect.class);
        props.put(Environment.DATASOURCE, dataSource);
        props.put(Environment.UNIQUE_CONSTRAINT_SCHEMA_UPDATE_STRATEGY, UniqueConstraintSchemaUpdateStrategy.RECREATE_QUIETLY);
        DefaultPersistenceUnitManager manager = new DefaultPersistenceUnitManager();
        manager.setPackagesToScan(pkgs.toArray(new String[]{}));
        manager.afterPropertiesSet();
        MutablePersistenceUnitInfo persistenceUnitInfo = (MutablePersistenceUnitInfo) manager.obtainDefaultPersistenceUnitInfo();
        managerClasses.forEach(clazz -> persistenceUnitInfo.addManagedClassName(clazz.getName()));
        EntityManagerFactoryBuilder builder = Bootstrap.getEntityManagerFactoryBuilder(persistenceUnitInfo, props);
        return (EntityManagerFactoryBuilderImpl) builder;
    }
}
