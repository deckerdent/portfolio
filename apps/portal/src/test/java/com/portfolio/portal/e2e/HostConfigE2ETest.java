package com.portfolio.portal.e2e;

import com.portfolio.portal.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HostConfigE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("portal_e2e")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired
    private WebTestClient webTestClient;

    /** Wipe the table between tests via the API so each test starts clean. */
    @BeforeEach
    void cleanUp() {
        webTestClient.get()
                .uri("/api/host/config")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Map.class)
                .value((List<Map> props) -> props.forEach(prop -> webTestClient.delete()
                        .uri("/api/host/config/" + prop.get("key"))
                        .exchange()));
    }

    // ── tests ────────────────────────────────────────────────────────────────

    @Test
    void initializedIsFalse_onEmptyDatabase() {
        webTestClient.get()
                .uri("/api/host/initialized")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.initialized").isEqualTo(false);
    }

    @Test
    void happyPath_seed_read_update_delete_checkInitialized() {
        // Seed required props
        seedProp("title", "E2E Portal");
        seedProp("basePath", "/");

        // Now initialized
        webTestClient.get()
                .uri("/api/host/initialized")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.initialized").isEqualTo(true);

        // GET /host reflects seeded values
        webTestClient.get()
                .uri("/api/host")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("E2E Portal")
                .jsonPath("$.basePath").isEqualTo("/");

        // Update basePath
        webTestClient.put()
                .uri("/api/host/config/basePath")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"key\": \"basePath\", \"value\": \"/portal\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.value").isEqualTo("/portal");

        // GET /host now returns the updated basePath
        webTestClient.get()
                .uri("/api/host")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.basePath").isEqualTo("/portal");

        // Delete title → should become uninitialized
        webTestClient.delete()
                .uri("/api/host/config/title")
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/api/host/initialized")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.initialized").isEqualTo(false);
    }

    @Test
    void sourceUrls_storedAsCommaSeparated_returnedAsArray() {
        seedProp("sourceUrls", "/sources.json,/extra.json");

        webTestClient.get()
                .uri("/api/host")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.sourceUrls[0]").isEqualTo("/sources.json")
                .jsonPath("$.sourceUrls[1]").isEqualTo("/extra.json")
                .jsonPath("$.configProps.sourceUrls").isEqualTo("/sources.json,/extra.json");
    }

    @Test
    void getHostConfig_returnsAllProps_afterMultipleUpserts() {
        seedProp("title", "Portal");
        seedProp("basePath", "/");
        seedProp("appsUrl", "/custom-apps.json");

        webTestClient.get()
                .uri("/api/host/config")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3);
    }

    @Test
    void upsert_isIdempotent_updatesExistingKey() {
        seedProp("title", "First");
        seedProp("title", "Second");

        webTestClient.get()
                .uri("/api/host/config")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].value").isEqualTo("Second");
    }

    @Test
    void updateConfig_returns404_whenKeyAbsent() {
        webTestClient.put()
                .uri("/api/host/config/ghost")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"key\": \"ghost\", \"value\": \"x\"}")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteConfig_returns404_whenKeyAbsent() {
        webTestClient.delete()
                .uri("/api/host/config/ghost")
                .exchange()
                .expectStatus().isNotFound();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void seedProp(String key, String value) {
        webTestClient.post()
                .uri("/api/host/config")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"key\": \"" + key + "\", \"value\": \"" + value + "\"}")
                .exchange()
                .expectStatus().isCreated();
    }
}
