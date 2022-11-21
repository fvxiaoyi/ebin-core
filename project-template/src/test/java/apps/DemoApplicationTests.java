package apps;

import apps.example.domain.Example;
import apps.example.domain.service.ExampleRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class DemoApplicationTests {

    @Autowired
    ExampleRepo exampleRepo;

    @Test
    public void test() {
        List<Example> examples = exampleRepo.selectByNamedQuery("ExampleFinder.selectByName", "1");
        System.out.println(examples);
    }
}
