package apps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureDataJpa
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQLDB)
class DemoApplicationTests {
    @Test
    void contextLoads() {

    }
}
