package ru.yandex.practicum.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.yandex.practicum.gateway.config.GatewaySecurityConfig;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
class GatewaySecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void products_areAvailableWithoutAuthentication() {
        webTestClient.get()
                .uri("/api/products")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createOrder_withoutAuthentication_isUnauthorized() {
        webTestClient.post()
                .uri("/api/orders")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void writeProduct_asUser_isForbidden() {
        webTestClient.patch()
                .uri("/api/products/10")
                .headers(headers -> headers.setBasicAuth("ivan", "ivan"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void writeProduct_asAdmin_passesSecurityCheck() {
        webTestClient.patch()
                .uri("/api/products/10")
                .headers(headers -> headers.setBasicAuth("anna", "anna"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void unknownRoute_withAdminCredentials_isForbidden() {
        webTestClient.get()
                .uri("/api/unknown")
                .headers(headers -> headers.setBasicAuth("anna", "anna"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void preflight_isNotBlockedBySecurity() {
        webTestClient.options()
                .uri("/api/orders")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization, content-type")
                .exchange()
                .expectStatus().isOk();
    }

    @TestConfiguration
    @Import(GatewaySecurityConfig.class)
    static class TestConfig {
        @Bean
        RouterFunction<ServerResponse> testRoutes() {
            return route()
                    .GET("/api/products", request -> ServerResponse.ok().build())
                    .GET("/api/products/10", request -> ServerResponse.ok().build())
                    .PATCH("/api/products/10", request -> ServerResponse.ok().build())
                    .POST("/api/orders", request -> ServerResponse.ok().build())
                    .GET("/api/orders", request -> ServerResponse.ok().build())
                    .OPTIONS("/api/orders", request -> ServerResponse.ok().build())
                    .build();
        }
    }
}