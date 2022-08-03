package apps.example.domain.service;

import apps.example.domain.Example;
import core.framework.domain.Repository;

import java.util.List;

/**
 * @author ebin
 */
public interface ExampleRepo extends Repository<Example> {
    default List<Example> selectByName(String name) {
        return this.selectByNamedQuery("ExampleFinder.selectByName", name);
    }

    default List<Example> selectByNameV2(String name) {
        return this.selectByQueryString("FROM Example e WHERE e.name = ?0", name);
    }
}
