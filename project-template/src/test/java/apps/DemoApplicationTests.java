package apps;

import apps.example.domain.Example;
import apps.example.domain.service.ExampleRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    ExampleRepo exampleRepo;

    @Test
    void contextLoads() {
        List<Example> test = exampleRepo.selectByName("TEST");
        System.out.println(test);
    }

}
