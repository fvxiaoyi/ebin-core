package apps.config;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import javax.persistence.Converter;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public class ConfigurablePersistenceUnitManager implements PersistenceUnitManager, ResourceLoaderAware, LoadTimeWeaverAware {
    private static final String CLASS_RESOURCE_PATTERN = "/**/*.class";
    private static final String XML_RESOURCE_PATTERN = "/**/*Finder.xml";
    private static final String PACKAGE_INFO_SUFFIX = ".package-info";
    private static final Set<AnnotationTypeFilter> entityTypeFilters;

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private CandidateComponentsIndex componentsIndex;
    private LoadTimeWeaver loadTimeWeaver;
    private final Map<String, PersistenceUnitInfo> persistenceUnitInfos = new HashMap<>();

    static {
        entityTypeFilters = new LinkedHashSet<>(8);
        entityTypeFilters.add(new AnnotationTypeFilter(Entity.class, false));
        entityTypeFilters.add(new AnnotationTypeFilter(Embeddable.class, false));
        entityTypeFilters.add(new AnnotationTypeFilter(MappedSuperclass.class, false));
        entityTypeFilters.add(new AnnotationTypeFilter(Converter.class, false));
    }

    @Override
    public PersistenceUnitInfo obtainDefaultPersistenceUnitInfo() {
        PersistenceUnitInfo pui = this.persistenceUnitInfos.values().iterator().next();
        this.persistenceUnitInfos.clear();
        return pui;
    }

    @Override
    public PersistenceUnitInfo obtainPersistenceUnitInfo(String persistenceUnitName) {
        PersistenceUnitInfo pui = this.persistenceUnitInfos.remove(persistenceUnitName);
        if (pui == null) {
            throw new IllegalArgumentException("No persistence unit with name '" + persistenceUnitName + "' found");
        }
        return pui;
    }

    public void addPersistenceUnitInfo(String persistenceUnitName, String[] packagesToScan, DataSource dataSource) {
//        ConfigurablePersistenceUnitInfo persistenceUnitInfo = new ConfigurablePersistenceUnitInfo();
//        persistenceUnitInfo.setPersistenceUnitName(persistenceUnitName);
//        persistenceUnitInfo.setExcludeUnlistedClasses(true);
//
//        persistenceUnitInfo.setNonJtaDataSource(dataSource);
//
//        if (packagesToScan != null) {
//            for (String pkg : packagesToScan) {
//                scanPackage(persistenceUnitInfo, pkg);
//                scanMappingResources(persistenceUnitInfo, pkg);
//            }
//        }
//        this.persistenceUnitInfos.put(persistenceUnitName, persistenceUnitInfo);
    }

    private void scanMappingResources(MutablePersistenceUnitInfo persistenceUnitInfo, String pkg) {
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(pkg) + XML_RESOURCE_PATTERN;
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(pattern);
            for (Resource resource : resources) {
                String url = resource.getURL().toString();
                //todo
                url = url.substring(url.indexOf("apps"));
                persistenceUnitInfo.addMappingFileName(url);
            }
        } catch (IOException e) {
            //ignore
            throw new PersistenceException("Failed to scan classpath for unlisted entity classes", e);
        }
    }

    private void scanPackage(MutablePersistenceUnitInfo persistenceUnitInfo, String pkg) {
        if (this.componentsIndex != null) {
            Set<String> candidates = new HashSet<>();
            for (AnnotationTypeFilter filter : entityTypeFilters) {
                candidates.addAll(this.componentsIndex.getCandidateTypes(pkg, filter.getAnnotationType().getName()));
            }
            candidates.forEach(persistenceUnitInfo::addManagedClassName);
            Set<String> managedPackages = this.componentsIndex.getCandidateTypes(pkg, "package-info");
            managedPackages.forEach(persistenceUnitInfo::addManagedPackage);
            return;
        }

        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(pkg) + CLASS_RESOURCE_PATTERN;
            Resource[] resources = this.resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
            for (Resource resource : resources) {
                try {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    if (matchesFilter(reader, readerFactory)) {
                        persistenceUnitInfo.addManagedClassName(className);
                        if (persistenceUnitInfo.getPersistenceUnitRootUrl() == null) {
                            URL url = resource.getURL();
                            if (ResourceUtils.isJarURL(url)) {
                                persistenceUnitInfo.setPersistenceUnitRootUrl(ResourceUtils.extractJarFileURL(url));
                            }
                        }
                    } else if (className.endsWith(PACKAGE_INFO_SUFFIX)) {
                        persistenceUnitInfo.addManagedPackage(
                                className.substring(0, className.length() - PACKAGE_INFO_SUFFIX.length()));
                    }
                } catch (FileNotFoundException ex) {
                    // Ignore non-readable resource
                }
            }
        } catch (IOException ex) {
            throw new PersistenceException("Failed to scan classpath for unlisted entity classes", ex);
        }
    }

    private boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        for (TypeFilter filter : entityTypeFilters) {
            if (filter.match(reader, readerFactory)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.componentsIndex = CandidateComponentsIndexLoader.loadIndex(resourceLoader.getClassLoader());
    }

    @Override
    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }
}
