package core.framework.jpa.mongodb;

import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.internal.TcclLookupPrecedence;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Make the custom service after the one provided by hibernate.
 *
 * @author ebin
 */
public class ExtendClassLoaderServiceImpl implements ClassLoaderService {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ClassLoaderServiceImpl.class);

    private final ConcurrentMap<Class, ServiceLoader> serviceLoaders = new ConcurrentHashMap<Class, ServiceLoader>();
    private volatile AggregatedClassLoader aggregatedClassLoader;

    /**
     * Constructs a ClassLoaderServiceImpl with standard set-up
     */
    public ExtendClassLoaderServiceImpl() {
        this(ClassLoaderServiceImpl.class.getClassLoader());
    }

    /**
     * Constructs a ClassLoaderServiceImpl with the given ClassLoader
     *
     * @param classLoader The ClassLoader to use
     */
    public ExtendClassLoaderServiceImpl(ClassLoader classLoader) {
        this(Collections.singletonList(classLoader), TcclLookupPrecedence.AFTER);
    }

    /**
     * Constructs a ClassLoaderServiceImpl with the given ClassLoader instances
     *
     * @param providedClassLoaders The ClassLoader instances to use
     * @param lookupPrecedence     The lookup precedence of the thread context {@code ClassLoader}
     */
    public ExtendClassLoaderServiceImpl(Collection<ClassLoader> providedClassLoaders, TcclLookupPrecedence lookupPrecedence) {
        final LinkedHashSet<ClassLoader> orderedClassLoaderSet = new LinkedHashSet<ClassLoader>();

        // first, add all provided class loaders, if any
        if (providedClassLoaders != null) {
            for (ClassLoader classLoader : providedClassLoaders) {
                if (classLoader != null) {
                    orderedClassLoaderSet.add(classLoader);
                }
            }
        }

        // normalize adding known class-loaders...
        // then the Hibernate class loader
        orderedClassLoaderSet.add(ClassLoaderServiceImpl.class.getClassLoader());

        // now build the aggregated class loader...
        this.aggregatedClassLoader = AccessController.doPrivileged(new PrivilegedAction<AggregatedClassLoader>() {
            public AggregatedClassLoader run() {
                return new AggregatedClassLoader(orderedClassLoaderSet, lookupPrecedence);
            }
        });
    }

    /**
     * No longer used/supported!
     *
     * @param configValues The config values
     * @return The built service
     * @deprecated No longer used/supported!
     */
    @Deprecated
    @SuppressWarnings({"UnusedDeclaration", "unchecked", "deprecation"})
    public static ClassLoaderServiceImpl fromConfigSettings(Map configValues) {
        final List<ClassLoader> providedClassLoaders = new ArrayList<ClassLoader>();

        final Collection<ClassLoader> classLoaders = (Collection<ClassLoader>) configValues.get(AvailableSettings.CLASSLOADERS);
        if (classLoaders != null) {
            for (ClassLoader classLoader : classLoaders) {
                providedClassLoaders.add(classLoader);
            }
        }

        addIfSet(providedClassLoaders, AvailableSettings.APP_CLASSLOADER, configValues);
        addIfSet(providedClassLoaders, AvailableSettings.RESOURCES_CLASSLOADER, configValues);
        addIfSet(providedClassLoaders, AvailableSettings.HIBERNATE_CLASSLOADER, configValues);
        addIfSet(providedClassLoaders, AvailableSettings.ENVIRONMENT_CLASSLOADER, configValues);

        return new ClassLoaderServiceImpl(providedClassLoaders, TcclLookupPrecedence.AFTER);
    }

    private static void addIfSet(List<ClassLoader> providedClassLoaders, String name, Map configVales) {
        final ClassLoader providedClassLoader = (ClassLoader) configVales.get(name);
        if (providedClassLoader != null) {
            providedClassLoaders.add(providedClassLoader);
        }
    }

    private static ClassLoader locateSystemClassLoader() {
        try {
            return ClassLoader.getSystemClassLoader();
        } catch (Exception e) {
            return null;
        }
    }

    private static ClassLoader locateTCCL() {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> Class<T> classForName(String className) {
        try {
            return (Class<T>) Class.forName(className, true, getAggregatedClassLoader());
        } catch (Exception e) {
            throw new ClassLoadingException("Unable to load class [" + className + "]", e);
        } catch (LinkageError e) {
            throw new ClassLoadingException("Unable to load class [" + className + "]", e);
        }
    }

    @Override
    public URL locateResource(String name) {
        // first we try name as a URL
        try {
            return new URL(name);
        } catch (Exception ignore) {
        }

        try {
            final URL url = getAggregatedClassLoader().getResource(name);
            if (url != null) {
                return url;
            }
        } catch (Exception ignore) {
        }

        if (name.startsWith("/")) {
            String substringName = name.substring(1);

            try {
                final URL url = getAggregatedClassLoader().getResource(substringName);
                if (url != null) {
                    return url;
                }
            } catch (Exception ignore) {
            }
        }

        return null;
    }

    @Override
    public InputStream locateResourceStream(String name) {
        // first we try name as a URL
        try {
            LOG.tracef("trying via [new URL(\"%s\")]", name);
            return new URL(name).openStream();
        } catch (Exception ignore) {
        }

        try {
            LOG.tracef("trying via [ClassLoader.getResourceAsStream(\"%s\")]", name);
            final InputStream stream = getAggregatedClassLoader().getResourceAsStream(name);
            if (stream != null) {
                return stream;
            }
        } catch (Exception ignore) {
        }

        final String stripped = name.startsWith("/") ? name.substring(1) : null;

        if (stripped != null) {
            try {
                LOG.tracef("trying via [new URL(\"%s\")]", stripped);
                return new URL(stripped).openStream();
            } catch (Exception ignore) {
            }

            try {
                LOG.tracef("trying via [ClassLoader.getResourceAsStream(\"%s\")]", stripped);
                final InputStream stream = getAggregatedClassLoader().getResourceAsStream(stripped);
                if (stream != null) {
                    return stream;
                }
            } catch (Exception ignore) {
            }
        }

        return null;
    }

    @Override
    public List<URL> locateResources(String name) {
        final ArrayList<URL> urls = new ArrayList<URL>();
        try {
            final Enumeration<URL> urlEnumeration = getAggregatedClassLoader().getResources(name);
            if (urlEnumeration != null && urlEnumeration.hasMoreElements()) {
                while (urlEnumeration.hasMoreElements()) {
                    urls.add(urlEnumeration.nextElement());
                }
            }
        } catch (Exception ignore) {
        }

        return urls;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> Collection<S> loadJavaServices(Class<S> serviceContract) {
        ServiceLoader<S> serviceLoader = serviceLoaders.get(serviceContract);
        if (serviceLoader == null) {
            serviceLoader = ServiceLoader.load(serviceContract, getAggregatedClassLoader());
            serviceLoaders.put(serviceContract, serviceLoader);
        }
        final LinkedHashSet<S> services = new LinkedHashSet<S>();
        for (S service : serviceLoader) {
            services.add(service);
        }
        return services;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T generateProxy(InvocationHandler handler, Class... interfaces) {
        return (T) Proxy.newProxyInstance(
                getAggregatedClassLoader(),
                interfaces,
                handler
        );
    }

    @Override
    public <T> T workWithClassLoader(Work<T> work) {
        return work.doWork(getAggregatedClassLoader());
    }

    private ClassLoader getAggregatedClassLoader() {
        final AggregatedClassLoader aggregated = this.aggregatedClassLoader;
        if (aggregated == null) {
            throw LOG.usingStoppedClassLoaderService();
        }
        return aggregated;
    }

    @Override
    public void stop() {
        for (ServiceLoader serviceLoader : serviceLoaders.values()) {
            serviceLoader.reload(); // clear service loader providers
        }
        serviceLoaders.clear();
        //Avoid ClassLoader leaks
        this.aggregatedClassLoader = null;
    }

    private static final class AggregatedClassLoader extends ClassLoader {
        private final ClassLoader[] individualClassLoaders;
        private final TcclLookupPrecedence tcclLookupPrecedence;

        private AggregatedClassLoader(final LinkedHashSet<ClassLoader> orderedClassLoaderSet, TcclLookupPrecedence precedence) {
            super(null);
            individualClassLoaders = orderedClassLoaderSet.toArray(new ClassLoader[orderedClassLoaderSet.size()]);
            tcclLookupPrecedence = precedence;
        }

        private Iterator<ClassLoader> newClassLoaderIterator() {
            final ClassLoader threadClassLoader = locateTCCL();
            if (tcclLookupPrecedence == TcclLookupPrecedence.NEVER || threadClassLoader == null) {
                return newTcclNeverIterator();
            } else if (tcclLookupPrecedence == TcclLookupPrecedence.AFTER) {
                return newTcclAfterIterator(threadClassLoader);
            } else if (tcclLookupPrecedence == TcclLookupPrecedence.BEFORE) {
                return newTcclBeforeIterator(threadClassLoader);
            } else {
                throw new RuntimeException("Unknown precedence: " + tcclLookupPrecedence);
            }
        }

        private Iterator<ClassLoader> newTcclBeforeIterator(final ClassLoader threadContextClassLoader) {
            final ClassLoader systemClassLoader = locateSystemClassLoader();
            return new Iterator<ClassLoader>() {
                private int currentIndex;
                private boolean tcCLReturned;
                private boolean sysCLReturned;

                @Override
                public boolean hasNext() {
                    if (!tcCLReturned) {
                        return true;
                    } else if (currentIndex < individualClassLoaders.length) {
                        return true;
                    } else if (!sysCLReturned && systemClassLoader != null) {
                        return true;
                    }

                    return false;
                }

                @Override
                public ClassLoader next() {
                    if (!tcCLReturned) {
                        tcCLReturned = true;
                        return threadContextClassLoader;
                    } else if (currentIndex < individualClassLoaders.length) {
                        currentIndex += 1;
                        return individualClassLoaders[currentIndex - 1];
                    } else if (!sysCLReturned && systemClassLoader != null) {
                        sysCLReturned = true;
                        return systemClassLoader;
                    }
                    throw new IllegalStateException("No more item");
                }
            };
        }

        private Iterator<ClassLoader> newTcclAfterIterator(final ClassLoader threadContextClassLoader) {
            final ClassLoader systemClassLoader = locateSystemClassLoader();
            return new Iterator<ClassLoader>() {
                private int currentIndex;
                private boolean tcCLReturned;
                private boolean sysCLReturned;

                @Override
                public boolean hasNext() {
                    if (currentIndex < individualClassLoaders.length) {
                        return true;
                    } else if (!tcCLReturned) {
                        return true;
                    } else if (!sysCLReturned && systemClassLoader != null) {
                        return true;
                    }

                    return false;
                }

                @Override
                public ClassLoader next() {
                    if (currentIndex < individualClassLoaders.length) {
                        currentIndex += 1;
                        return individualClassLoaders[currentIndex - 1];
                    } else if (!tcCLReturned) {
                        tcCLReturned = true;
                        return threadContextClassLoader;
                    } else if (!sysCLReturned && systemClassLoader != null) {
                        sysCLReturned = true;
                        return systemClassLoader;
                    }
                    throw new IllegalStateException("No more item");
                }
            };
        }

        private Iterator<ClassLoader> newTcclNeverIterator() {
            final ClassLoader systemClassLoader = locateSystemClassLoader();
            return new Iterator<ClassLoader>() {
                private int currentIndex;
                private boolean sysCLReturned;

                @Override
                public boolean hasNext() {
                    if (currentIndex < individualClassLoaders.length) {
                        return true;
                    } else if (!sysCLReturned && systemClassLoader != null) {
                        return true;
                    }

                    return false;
                }

                @Override
                public ClassLoader next() {
                    if (currentIndex < individualClassLoaders.length) {
                        currentIndex += 1;
                        return individualClassLoaders[currentIndex - 1];
                    } else if (!sysCLReturned && systemClassLoader != null) {
                        sysCLReturned = true;
                        return systemClassLoader;
                    }
                    throw new IllegalStateException("No more item");
                }
            };
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            final LinkedHashSet<URL> resourceUrls = new LinkedHashSet<URL>();
            final Iterator<ClassLoader> clIterator = newClassLoaderIterator();
            while (clIterator.hasNext()) {
                final ClassLoader classLoader = clIterator.next();
                final Enumeration<URL> urls = classLoader.getResources(name);
                while (urls.hasMoreElements()) {
                    resourceUrls.add(urls.nextElement());
                }
            }

            List<URL> sortedUrls = resourceUrls.stream().sorted((o1, o2) -> o1.getPath().contains("core-module") ? 1 : -1).collect(Collectors.toList());

            return new Enumeration<URL>() {
                final Iterator<URL> resourceUrlIterator = sortedUrls.iterator();

                @Override
                public boolean hasMoreElements() {
                    return resourceUrlIterator.hasNext();
                }

                @Override
                public URL nextElement() {
                    return resourceUrlIterator.next();
                }
            };
        }

        @Override
        protected URL findResource(String name) {
            final Iterator<ClassLoader> clIterator = newClassLoaderIterator();
            while (clIterator.hasNext()) {
                final ClassLoader classLoader = clIterator.next();
                final URL resource = classLoader.getResource(name);
                if (resource != null) {
                    return resource;
                }
            }
            return super.findResource(name);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            final Iterator<ClassLoader> clIterator = newClassLoaderIterator();
            while (clIterator.hasNext()) {
                final ClassLoader classLoader = clIterator.next();
                try {
                    return classLoader.loadClass(name);
                } catch (Exception ignore) {
                } catch (LinkageError ignore) {
                }
            }

            throw new ClassNotFoundException("Could not load requested class : " + name);
        }

    }
}
