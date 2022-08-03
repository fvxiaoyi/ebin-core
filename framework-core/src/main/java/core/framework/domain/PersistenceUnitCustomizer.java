package core.framework.domain;

import core.framework.domain.impl.DomainEventTracking;
import core.framework.utils.ResourcePatternResolverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import java.io.IOException;
import java.util.List;

/**
 * @author ebin
 */
public class PersistenceUnitCustomizer implements PersistenceUnitPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(PersistenceUnitCustomizer.class);

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        List<Resource> resources = ResourcePatternResolverUtil.resolve("**/*Finder.xml");
        resources.forEach(resource -> {
            try {
                String url = resource.getURL().toString();
                //todo
                url = url.substring(url.indexOf("apps"));
                logger.info("add mapping filename=" + url);
                pui.addMappingFileName(url);
            } catch (IOException e) {
                //ignore
            }
        });

        pui.addManagedClassName(DomainEventTracking.class.getName());
    }
}
