package core.framework.jpa.support;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.DecoratingClassLoader;
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
import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import javax.persistence.Converter;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.spi.ClassTransformer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author ebin
 */
public class ConfigurablePersistenceUnitInfo extends MutablePersistenceUnitInfo implements ResourceLoaderAware, LoadTimeWeaverAware {
    private static final String CLASS_RESOURCE_PATTERN = "/**/*.class";
    private static final String XML_RESOURCE_PATTERN = "/**/*Finder.xml";
    private static final String PACKAGE_INFO_SUFFIX = ".package-info";
    private static final Set<AnnotationTypeFilter> entityTypeFilters;

    static {
        entityTypeFilters = new LinkedHashSet<>(8);
        entityTypeFilters.add(new AnnotationTypeFilter(Entity.class, false));
        entityTypeFilters.add(new AnnotationTypeFilter(Embeddable.class, false));
        entityTypeFilters.add(new AnnotationTypeFilter(MappedSuperclass.class, false));
        entityTypeFilters.add(new AnnotationTypeFilter(Converter.class, false));
    }

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private CandidateComponentsIndex componentsIndex;
    private LoadTimeWeaver loadTimeWeaver;
    private ClassLoader classLoader = resourcePatternResolver.getClassLoader();
    private String basePackagePath;

    public ConfigurablePersistenceUnitInfo(String persistenceUnitName) {
        this.setPersistenceUnitName(persistenceUnitName);
        this.setExcludeUnlistedClasses(true);
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        ClassLoader tcl = (this.loadTimeWeaver != null ? this.loadTimeWeaver.getThrowawayClassLoader() :
                new SimpleThrowawayClassLoader(this.classLoader));
        String packageToExclude = getPersistenceProviderPackageName();
        if (packageToExclude != null && tcl instanceof DecoratingClassLoader) {
            ((DecoratingClassLoader) tcl).excludePackage(packageToExclude);
        }
        return tcl;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public void addTransformer(ClassTransformer classTransformer) {
        if (this.loadTimeWeaver == null) {
            throw new IllegalStateException("Cannot apply class transformer without LoadTimeWeaver specified");
        }
        this.loadTimeWeaver.addTransformer(new ClassFileTransformerAdapter(classTransformer));
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.componentsIndex = CandidateComponentsIndexLoader.loadIndex(resourceLoader.getClassLoader());
    }

    @Override
    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
        this.classLoader = loadTimeWeaver.getInstrumentableClassLoader();
    }

    public void setBasePackagePath(String basePackagePath) {
        this.basePackagePath = Objects.requireNonNull(basePackagePath).replace(".", "/");
    }

    public void setPackagesToScan(List<String> packagesToScan) {
        if (packagesToScan != null) {
            for (String pkg : packagesToScan) {
                scanPackage(pkg);
                scanMappingResources(pkg);
            }
        }
    }

    private void scanMappingResources(String pkg) {
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(pkg) + XML_RESOURCE_PATTERN;
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(pattern);
            for (Resource resource : resources) {
                String url = resource.getURL().toString();
                url = url.substring(url.indexOf(basePackagePath));
                this.addMappingFileName(url);
            }
        } catch (IOException e) {
            //ignore
            throw new PersistenceException("Failed to scan classpath for unlisted entity class mapping resources", e);
        }
    }

    private void scanPackage(String pkg) {
        if (this.componentsIndex != null) {
            Set<String> candidates = new HashSet<>();
            for (AnnotationTypeFilter filter : entityTypeFilters) {
                candidates.addAll(this.componentsIndex.getCandidateTypes(pkg, filter.getAnnotationType().getName()));
            }
            candidates.forEach(this::addManagedClassName);
            Set<String> managedPackages = this.componentsIndex.getCandidateTypes(pkg, "package-info");
            managedPackages.forEach(this::addManagedPackage);
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
                        this.addManagedClassName(className);
                        if (this.getPersistenceUnitRootUrl() == null) {
                            URL url = resource.getURL();
                            if (ResourceUtils.isJarURL(url)) {
                                this.setPersistenceUnitRootUrl(ResourceUtils.extractJarFileURL(url));
                            }
                        }
                    } else if (className.endsWith(PACKAGE_INFO_SUFFIX)) {
                        this.addManagedPackage(
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
}
