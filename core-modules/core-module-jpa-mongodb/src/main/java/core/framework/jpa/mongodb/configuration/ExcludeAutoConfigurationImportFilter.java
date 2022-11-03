package core.framework.jpa.mongodb.configuration;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import java.util.Objects;
import java.util.Set;

/**
 * @author ebin
 */
public class ExcludeAutoConfigurationImportFilter implements AutoConfigurationImportFilter {

    private static final Set<String> SHOULD_SKIP = Set.of("org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration");

    @Override
    public boolean[] match(String[] classNames, AutoConfigurationMetadata metadata) {
        boolean[] matches = new boolean[classNames.length];

        for (int i = 0; i < classNames.length; i++) {
            String className = classNames[i];
            if (Objects.nonNull(className)) {
                matches[i] = !SHOULD_SKIP.contains(className);
            }
        }
        return matches;
    }
}