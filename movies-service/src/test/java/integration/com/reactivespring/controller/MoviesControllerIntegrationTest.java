package com.reactivespring.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.junit.jupiter.api.BeforeEach;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebClient
@AutoConfigureWireMock(port = 8084) // automaticaly spins up a httpserver in port 8084
@TestPropertySource(properties = {
        "restClient.moviesInfoUrl=http://localhost:8084/v1/movieinfos",
        "restClient.reviewsUrl=http://localhost:8084/v1/reviews",
})
public class MoviesControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    public void retrieveMovieById(){
        var movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movieinfos/"+ movieId)).willReturn(aResponse()
                .withHeader("Content-Type","application/json")
                .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews")).willReturn(aResponse()
                .withHeader("Content-Type","application/json")
                .withBodyFile("reviews.json")));

        webTestClient.get().uri("/v1/movies/{id}",movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(movie).getReviewList().size()==2;
                    assertEquals("Batman Begins",movie.getMovieInfo().getName());
                });
    }

    @Test
    public void retrieveMovieById_moviesinfo_404(){
        var movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movieinfos/"+ movieId)).willReturn(aResponse()
                .withStatus(404)));

        stubFor(get(urlPathEqualTo("/v1/reviews")).willReturn(aResponse()
                .withHeader("Content-Type","application/json")
                .withBodyFile("reviews.json")));

        webTestClient.get().uri("/v1/movies/{id}",movieId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("There is no MovieInfo available for the passed in Id : abc");

    }

    @Test
    public void retrieveMovieById_reviews_404(){
        var movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movieinfos/"+ movieId)).willReturn(aResponse()
                .withHeader("Content-Type","application/json")
                .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .withQueryParam("movieInfoId", equalTo(movieId))
                .willReturn(aResponse()
                        .withStatus(404)));

        webTestClient.get().uri("/v1/movies/{id}",movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(movie).getReviewList().size()==0;
                    assertEquals("Batman Begins",movie.getMovieInfo().getName());
                });
    }

    @Test
    public void retrieveMovieById_5XX(){
        var movieId = "abc";

        stubFor(get(urlEqualTo("/v1/movieinfos/"+ movieId)).willReturn(aResponse()
                .withStatus(500)
                .withBody("Movie info service unavailable")));

        webTestClient.get().uri("/v1/movies/{id}",movieId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server exception in MoviesInfoService Movie info service unavailable");

        WireMock.verify(4,getRequestedFor(urlEqualTo("/v1/movieinfos/"+ movieId)));
    }
}
