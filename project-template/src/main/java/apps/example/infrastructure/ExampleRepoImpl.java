package apps.example.infrastructure;

import apps.example.domain.Example;
import apps.example.domain.service.ExampleRepo;
import core.framework.domain.impl.AbstractRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ebin
 */
@Repository
public class ExampleRepoImpl extends AbstractRepository<Example> implements ExampleRepo {
}
