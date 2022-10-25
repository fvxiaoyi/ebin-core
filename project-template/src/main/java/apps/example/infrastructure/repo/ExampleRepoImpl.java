package apps.example.infrastructure.repo;

import apps.example.domain.Example;
import apps.example.domain.service.ExampleRepo;
import com.framework.jpa.mysql.impl.AbstractMysqlRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ebin
 */
@Repository
public class ExampleRepoImpl extends AbstractMysqlRepository<Example> implements ExampleRepo {
}
