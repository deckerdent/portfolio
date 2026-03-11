package com.portfolio.cv.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CvE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("cv_e2e")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("cv.demo-data.enabled", () -> "false");
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("""
                TRUNCATE TABLE
                    general_info,
                    professional_experience,
                    education,
                    skill,
                    competence,
                    "language",
                    hobby,
                    "reference",
                    certificate
                RESTART IDENTITY CASCADE
                """);
    }

    @Test
    void generalInfo_returns404_whenMissing() {
        webTestClient.get()
                .uri("/api/cv/general-info")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void upsertGeneralInfo_thenReadBack() {
        webTestClient.put()
                .uri("/api/cv/general-info")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "firstName": "Max",
                          "lastName": "Mustermann",
                          "nationality": "DE",
                          "summary": "Platform engineer"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Max")
                .jsonPath("$.lastName").isEqualTo("Mustermann");

        webTestClient.get()
                .uri("/api/cv/general-info")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Max")
                .jsonPath("$.lastName").isEqualTo("Mustermann")
                .jsonPath("$.nationality").isEqualTo("DE")
                .jsonPath("$.summary").isEqualTo("Platform engineer");
    }

    @Test
    void createExperience_thenListIncludesEntry() {
        webTestClient.post()
                .uri("/api/cv/experiences")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "title": "Senior Developer",
                          "companyName": "Acme Corp",
                          "startDate": "2024-01-01",
                          "location": "Berlin",
                          "description": "Built modular systems"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo("Senior Developer")
                .jsonPath("$.companyName").isEqualTo("Acme Corp");

        webTestClient.get()
                .uri("/api/cv/experiences")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].title").isEqualTo("Senior Developer")
                .jsonPath("$[0].companyName").isEqualTo("Acme Corp");
    }
}