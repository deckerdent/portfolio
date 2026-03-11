package com.portfolio.portal;

import com.portfolio.portal.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.web.resources.static-locations=file:src/main/resources/static/"
})
class StaticResourcesTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() throws IOException {
        // Ensure static directory and test file exist
        Path staticDir = Path.of("src/main/resources/static/portal");
        Files.createDirectories(staticDir);

        Path testFile = staticDir.resolve("test.html");
        if (!Files.exists(testFile)) {
            Files.writeString(testFile, "<html><body>Test</body></html>");
        }
    }

    @Test
    void testStaticFolderAccessible() {
        webTestClient.get()
                .uri("/static/portal/test.html")
                .exchange()
                .expectStatus().isOk();
    }
}
