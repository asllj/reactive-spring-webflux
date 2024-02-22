package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@WebFluxTest
@AutoConfigureWebTestClient
class MovieInfoControllerUnitTest {

    public static final String MOVIE_INFO_URI = "/v1/movieinfos";

    @Autowired
    private WebTestClient client;

    @MockBean
    private MovieInfoService movieInfoServiceMock;

    @Test
    void createMovieInfo() {
        //given
        var movieInfo = new MovieInfo(null, "Batman Begins1", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        //when
        when(movieInfoServiceMock.saveMovieInfo(any(MovieInfo.class))).thenReturn(Mono.just(movieInfo));
        client.post().uri(MOVIE_INFO_URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void getMovieInfo() {
        var movieInfo = new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        when(movieInfoServiceMock.getMovieInfo(any())).thenReturn(Mono.just(movieInfo));

        client.get().uri(MOVIE_INFO_URI + "/{id}","abc")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedExchangeMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertEquals(savedExchangeMovieInfo.getName(),"Batman Begins");
                });
    }

    @Test
    void getAllMovieInfo() {
        //given
        var movieInfoList = List.of(new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
        new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));
        //when
        when(movieInfoServiceMock.getAllMovieInfo()).thenReturn(Flux.fromIterable(movieInfoList));
        client.get().uri(MOVIE_INFO_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }
    @Test
    void updateMovieInfo() {
        //given
        var id = "abc";
        var movieInfo = new MovieInfo(null, "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        when(movieInfoServiceMock.updateMovieInfo(isA(String.class),isA(MovieInfo.class))).thenReturn(
                Mono.just(new MovieInfo(id, "Dark Knight Rises", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))));
        //when
        client.put().uri(MOVIE_INFO_URI + "/{id}", id)
                .bodyValue(movieInfo)
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
    void deleteMovieInfo() {
        when(movieInfoServiceMock.deleteMovieInfo(isA(String.class)))
                .thenReturn(Mono.empty());

        //when
        client.delete().uri(MOVIE_INFO_URI + "/{id}","abc")
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}