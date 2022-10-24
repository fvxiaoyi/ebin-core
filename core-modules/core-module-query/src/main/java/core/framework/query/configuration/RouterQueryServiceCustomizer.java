package core.framework.query.configuration;


import core.framework.query.impl.RouterQueryService;

/**
 * @author ebin
 */
@FunctionalInterface
public interface RouterQueryServiceCustomizer {
    void customize(RouterQueryService routerQueryService);
}
