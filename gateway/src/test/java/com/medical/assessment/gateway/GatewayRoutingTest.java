package com.medical.assessment.gateway;

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
        "spring.cloud.gateway.server.webflux.routes[0].predicates[0]=Path=/patient/**"
})
@AutoConfigureWireMock(port = 0)
public class GatewayRoutingTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    void shouldRouteToPatientMs(){
        stubFor(get(urlEqualTo("/patient"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("[{\"id\":1}]")
                .withHeader("Content-Type", "application/json")));
        webClient.get()
                .uri("/patient")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].id").isEqualTo(1);
    }
}
