package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MovieInfoControllerIntegrationTest {

    public static final String MOVIE_INFO_URI = "/v1/movieinfos";
    @Autowired
    MovieInfoRepository repository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    public void setUp(){
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        repository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    public void tearDown(){
        repository.deleteAll().block();
    }

    @Test
    void createMovieInfo() {
        //given
        var input = new MovieInfo(null, "Batman Begins1", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        //when
        webTestClient.post().uri(MOVIE_INFO_URI)
                .bodyValue(input)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedExchangeMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedExchangeMovieInfo !=null;
                    assert savedExchangeMovieInfo.getMovieInfoId() != null ;
                });
    }

    @Test
    void getAllMovieInfo() {

        //when
        webTestClient.get().uri(MOVIE_INFO_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getAllMovieInfoByYear() {
        var uri =UriComponentsBuilder.fromUriString(MOVIE_INFO_URI)
                .queryParam("year",2005).buildAndExpand().toUri();
        //when
        webTestClient.get().uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getAllMovieInfoByName() {
        var uri =UriComponentsBuilder.fromUriString(MOVIE_INFO_URI)
                .queryParam("name","Batman Begins").buildAndExpand().toUri();
        //when
        webTestClient.get().uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMovieInfo() {
        //when
        webTestClient.get().uri(MOVIE_INFO_URI + "/{id}","abc")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedExchangeMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedExchangeMovieInfo !=null;
                    assert savedExchangeMovieInfo.getMovieInfoId() != null ;
                    assertEquals(savedExchangeMovieInfo.getName(),"Dark Knight Rises");
                });
    }

    @Test
    void getMovieInfo_withInvalidId() {
        //when
        webTestClient.get().uri(MOVIE_INFO_URI + "/{id}","def")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateMovieInfo() {
        //given
        var movieInfo = new MovieInfo("abc", "Dark Knight Rises2", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        //when
        webTestClient.put().uri(MOVIE_INFO_URI + "/{id}", "abc")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedExchangeMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedExchangeMovieInfo !=null;
                    assert savedExchangeMovieInfo.getMovieInfoId() != null ;
                    assertEquals(savedExchangeMovieInfo.getName(),"Dark Knight Rises2");
                });
    }

    @Test
    void deleteMovieInfo() {
        webTestClient.get().uri(MOVIE_INFO_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);

        //when
        webTestClient.delete().uri(MOVIE_INFO_URI + "/{id}","abc")
                .exchange()
                .expectStatus()
                .isNoContent();

        webTestClient.get().uri(MOVIE_INFO_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }

    @Test
    void updateMovieInfo_invalid() {
        //given
        var movieInfo = new MovieInfo("abc", "Dark Knight Rises2", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        //when
        webTestClient.put().uri(MOVIE_INFO_URI + "/{id}", "def")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();

    }
}