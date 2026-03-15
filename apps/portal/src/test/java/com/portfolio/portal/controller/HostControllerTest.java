package com.portfolio.portal.controller;

import com.portfolio.portal.config.PostgresIntegrationTestSupport;
import com.portfolio.portal.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class HostControllerTest extends PostgresIntegrationTestSupport {

        @Autowired
        private WebTestClient webTestClient;

        @Test
        void getInitialized_returnsFalse_onEmptyDatabase() {
                webTestClient.get()
                                .uri("/api/host/initialized")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.initialized").isEqualTo(false);
        }

        @Test
        void upsertConfig_returnsCreated_andPersistsValue() {
                webTestClient.post()
                                .uri("/api/host/config")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"key\": \"title\", \"value\": \"My Portal\"}")
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody()
                                .jsonPath("$.key").isEqualTo("title")
                                .jsonPath("$.value").isEqualTo("My Portal")
                                .jsonPath("$.id").isNotEmpty();
        }

        @Test
        void getHost_returnsCorrectResponse_afterSeedingTitleAndBasePath() {
                seedProp("title", "Portal");
                seedProp("basePath", "/");

                webTestClient.get()
                                .uri("/api/host")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.title").isEqualTo("Portal")
                                .jsonPath("$.basePath").isEqualTo("/")
                                .jsonPath("$.appsUrl").isEqualTo("/apps.json")
                                .jsonPath("$.sourceUrls").isArray()
                                .jsonPath("$.configProps.title").isEqualTo("Portal");
        }

        @Test
        void deleteConfig_returns204_andSubsequentInitializedIsFalse() {
                seedProp("title", "Portal");
                seedProp("basePath", "/");

                webTestClient.get()
                                .uri("/api/host/initialized")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody().jsonPath("$.initialized").isEqualTo(true);

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
        void updateConfig_returns200_withUpdatedValue() {
                seedProp("basePath", "/old");

                webTestClient.put()
                                .uri("/api/host/config/basePath")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"key\": \"basePath\", \"value\": \"/new\"}")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.value").isEqualTo("/new");
        }

        @Test
        void updateConfig_returns404_whenKeyDoesNotExist() {
                webTestClient.put()
                                .uri("/api/host/config/nonexistent")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"key\": \"nonexistent\", \"value\": \"x\"}")
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
