package com.portfolio.portal;

import com.portfolio.portal.config.PostgresIntegrationTestSupport;
import com.portfolio.portal.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
class SwaggerUiTest extends PostgresIntegrationTestSupport {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testSwaggerUiAccessible() {
        // Test that swagger-ui endpoint exists (may redirect or return content)
        webTestClient.get()
                .uri("/webjars/swagger-ui/index.html")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testApiDocsAccessible() {
        webTestClient.get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk();
    }
}
