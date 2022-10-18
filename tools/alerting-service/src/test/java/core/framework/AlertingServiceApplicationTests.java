package core.framework;

import core.framework.alerting.domain.service.GetAlertMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AlertingServiceApplicationTests {
    @Autowired
    GetAlertMessageService getAlertMessageService;

    @Test
    void contextLoads() {
    }
}
