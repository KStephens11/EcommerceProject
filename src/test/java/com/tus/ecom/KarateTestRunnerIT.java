package com.tus.ecom;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class KarateTestRunnerIT {

    @LocalServerPort
    int randomServerPort;

    @Karate.Test
    Karate runAll() {
        System.setProperty("local.server.port",
                String.valueOf(randomServerPort));

        return Karate.run("src/test/features/karate");
    }
}