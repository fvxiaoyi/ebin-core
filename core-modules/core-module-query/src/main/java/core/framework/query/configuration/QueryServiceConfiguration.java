package core.framework.query.configuration;

import core.framework.query.QueryParser;
import core.framework.query.impl.RouterQueryService;
import core.framework.query.impl.parser.MyBatisQueryParser;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author ebin
 */
@Configuration
public class QueryServiceConfiguration {
    @Bean
    public QueryParser queryParser() {
        return new MyBatisQueryParser();
    }

    @Bean
    @Primary
    public RouterQueryService routerQueryService(QueryParser queryParser,
                                                 ObjectProvider<RouterQueryServiceCustomizer> customizers) {
        RouterQueryService routerQueryService = new RouterQueryService(queryParser);
        customizers.orderedStream().forEach(customizer -> customizer.customize(routerQueryService));
        return routerQueryService;
    }
}
