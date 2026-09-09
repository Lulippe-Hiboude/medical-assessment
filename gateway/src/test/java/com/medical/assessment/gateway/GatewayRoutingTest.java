package com.medical.assessment.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.gateway.server.webflux.routes[0].id=patient-ms",
                "spring.cloud.gateway.server.webflux.routes[0].uri=http://localhost:${wiremock.server.port}",
                "spring.cloud.gateway.server.webflux.routes[0].predicates[0]=Path=/patient/**",
                "spring.cloud.gateway.server.webflux.routes[1].id=auth-ms",
                "spring.cloud.gateway.server.webflux.routes[1].uri=http://localhost:${wiremock.server.port}",
                "spring.cloud.gateway.server.webflux.routes[1].predicates[0]=Path=/auth/**",
                "spring.cloud.gateway.server.webflux.routes[2].id=note-ms",
                "spring.cloud.gateway.server.webflux.routes[2].uri=http://localhost:${wiremock.server.port}",
                "spring.cloud.gateway.server.webflux.routes[2].predicates[0]=Path=/notes/**",
                "spring.cloud.gateway.server.webflux.routes[3].id=risk-ms",
                "spring.cloud.gateway.server.webflux.routes[3].uri=http://localhost:${wiremock.server.port}",
                "spring.cloud.gateway.server.webflux.routes[3].predicates[0]=Path=/risk/**"
        })
@AutoConfigureWireMock(port = 0)
class GatewayRoutingTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    @DisplayName("Should route to patient microservice and return expected response")
    void shouldRouteToPatientMs() {
        stubFor(get(urlEqualTo("/patient"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":1}]")));

        webClient.get().uri("/patient")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].id").isEqualTo(1);
    }

    @Test
    @DisplayName("Should route to auth microservice and return expected response")
    void shouldRouteToAuthMs() {
        stubFor(post(urlEqualTo("/auth/login"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\":\"fake-jwt-token\"}")));

        webClient.post().uri("/auth/login")
                .bodyValue("{\"username\":\"doctor\",\"password\":\"password\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.token").isEqualTo("fake-jwt-token");
    }

    @Test
    @DisplayName("Should route to note microservice and return expected response")
    void shouldRouteToNoteMs() {
        stubFor(get(urlEqualTo("/notes/patient/1"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        webClient.get().uri("/notes/patient/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should route to risk microservice and return expected response")
    void shouldRouteToRiskMs() {
        stubFor(get(urlEqualTo("/risk/patient/1"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"riskLevel\":\"NONE\"}")));

        webClient.get().uri("/risk/patient/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.riskLevel").isEqualTo("NONE");
    }

    @Test
    @DisplayName("Should forward Authorization header to downstream service")
    void shouldForwardAuthorizationHeaderToDownstreamService() {
        stubFor(get(urlEqualTo("/patient"))
                .willReturn(aResponse().withStatus(200)));

        final String fakeToken = "Bearer fake-token";
        webClient.get().uri("/patient")
                .header("Authorization", fakeToken)
                .exchange()
                .expectStatus().isOk();

        verify(getRequestedFor(urlEqualTo("/patient"))
                .withHeader("Authorization", equalTo(fakeToken)));
    }

    @Test
    @DisplayName("Should return 404 for unmatched path")
    void shouldReturn404ForUnmatchedPath() {
        webClient.get().uri("/unknown")
                .exchange()
                .expectStatus().isNotFound();
    }
}
