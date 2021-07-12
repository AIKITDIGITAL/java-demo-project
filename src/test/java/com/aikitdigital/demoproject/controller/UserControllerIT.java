package com.aikitdigital.demoproject.controller;


import com.aikitdigital.demoproject.util.WireMockInitializer;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {WireMockInitializer.class})
public class UserControllerIT {

    /**
     * Manually initialized and registered into the context in {@link WireMockInitializer}
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() {
        wireMockServer.stubFor(
                WireMock.get("/users")
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBodyFile("test.json"))
        );
    }

    @AfterEach
    void afterEach() {
        this.wireMockServer.resetAll();
    }

    @Test
    @DisplayName("search users with name:Katarina and name:Sanjeev")
    void test0() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?search=name:Katarina,name:Sanjeev")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(4);
    }

    @Test
    @DisplayName("search users with offset:0 and limit:10")
    void test1() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?offset=0&limit=10")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(10);
    }

    @Test
    @DisplayName("search users with limit:101. This should return bad request because max limit is 25")
    void test2() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?limit=101")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("search users with offset:-1. This should return bad request because offset < 0")
    void test3() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?offset=-1")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("search users with limit:-1. This should return bad request because limit < 0")
    void test4() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?limit=-1")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("search users with offset:10 and limit:12")
    void test5() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?offset=10&limit=12")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(12)
                .jsonPath("$[0].userId").isEqualTo(10)
                .jsonPath("$[11].userId").isEqualTo(31);
    }

    @Test
    @DisplayName("search users with offset:49 and limit:10. Should return only last user")
    void test6() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?offset=49&limit=10")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[11].userId").isEqualTo(49);
    }

    @Test
    @DisplayName("search users without criteria which means default offset:0 and limit:25")
    void test7() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(25)
                .jsonPath("$[0].userId").isEqualTo(0)
                .jsonPath("$[24].userId").isEqualTo(34);
    }

    @Test
    @DisplayName("search users with userId:0 and userId:1")
    void test8() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?search=userId:0,userId:1")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(2)
                .jsonPath("$[0].userId")
                .isEqualTo(0)
                .jsonPath("$[1].userId")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("search users with userId:temp. Invalid userId, because it is not number")
    void test9() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?search=userId:temp")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("search user with userId:26")
    void test10() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users/26")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.userId").isEqualTo(26)
                .jsonPath("$.username").isEqualTo("ellametcalfe");
    }

    @Test
    @DisplayName("search users with userId>=15 and userId<=45 and offset:5 and limit:15 and order:DESC")
    void test11() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?search=userId>=15,userId<=45&offset=5&limit=15&order=userId:DESC")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$[1].userId").isEqualTo(21);
    }

    @Test
    @DisplayName("search users with userId>=15 and userId<=45 and offset:5 and limit:15 and order:ASC")
    void test13() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?search=userId>=15,userId<=45&offset=5&limit=15&order=userId:ASC")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$[1].userId").isEqualTo(39);
    }

    @Test
    @DisplayName("search users with to:null. Returns only active users")
    void test14() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?search=to:null")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(10);
    }

    @Test
    @DisplayName("search users with from>=2018-01-16T13:03:14.385+01:00")
    void test15() {
        this.webTestClient
                .get()
                .uri("http://localhost:" + port + "/api/users?search=from>=2018-01-16T13:03:14.385%2B01:00&order=from:DeSc")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(25)
                .jsonPath("$[0].userId").isEqualTo(21)
                .jsonPath("$[10].userId").isEqualTo(27)
                .jsonPath("$[24].userId").isEqualTo(30);
    }
}
