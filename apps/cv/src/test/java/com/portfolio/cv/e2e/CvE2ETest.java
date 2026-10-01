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

    private static final String GENERAL_INFO_URI = "/api/cv/general-info";
    private static final String EXPERIENCES_URI = "/api/cv/experiences";

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

    private final WebTestClient webTestClient;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    CvE2ETest(WebTestClient webTestClient, JdbcTemplate jdbcTemplate) {
        this.webTestClient = webTestClient;
        this.jdbcTemplate = jdbcTemplate;
    }

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
    void generalInfoReturns404WhenMissing() {
        webTestClient.get()
                .uri(GENERAL_INFO_URI)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void upsertGeneralInfoThenReadBack() {
        webTestClient.put()
                .uri(GENERAL_INFO_URI)
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
                .uri(GENERAL_INFO_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Max")
                .jsonPath("$.lastName").isEqualTo("Mustermann")
                .jsonPath("$.nationality").isEqualTo("DE")
                .jsonPath("$.summary").isEqualTo("Platform engineer");
    }

    @Test
    void createExperienceThenListIncludesEntry() {
        webTestClient.post()
                .uri(EXPERIENCES_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        """
                                {
                                  "title": "Senior Developer",
                                  "companyName": "Acme Corp",
                                  "startDate": "2024-01-01",
                                  "location": "Berlin",
                                                                                                                "description": "Built modular systems",
                                                                                                                "skills": ["Java", "Spring Boot", "PostgreSQL"],
                                                                                                                "highlights": "Reduced deployment incidents by introducing modular boundaries"
                                }
                                """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo("Senior Developer")
                .jsonPath("$.companyName").isEqualTo("Acme Corp")
                .jsonPath("$.skills.length()").isEqualTo(3)
                .jsonPath("$.skills[0]").isEqualTo("Java")
                .jsonPath("$.highlights").isEqualTo("Reduced deployment incidents by introducing modular boundaries");

        webTestClient.get()
                .uri(EXPERIENCES_URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].title").isEqualTo("Senior Developer")
                .jsonPath("$[0].companyName").isEqualTo("Acme Corp")
                .jsonPath("$[0].skills.length()").isEqualTo(3)
                .jsonPath("$[0].skills[1]").isEqualTo("Spring Boot")
                .jsonPath("$[0].highlights")
                .isEqualTo("Reduced deployment incidents by introducing modular boundaries");
    }
}