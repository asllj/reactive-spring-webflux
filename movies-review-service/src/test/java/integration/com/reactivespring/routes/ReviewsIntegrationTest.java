package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntegrationTest {


    public static final String REVIEW_URI = "/v1/reviews";
    @Autowired
    ReviewReactiveRepository repository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    public void setUp(){
        var reviews = List.of(new Review(null, 1L, "Awesome Movie",9.0),
                new Review("2", 1L, "Awesome Movie1",9.0),
                new Review(null, 2L, "Excellent Movie",8.0));

        repository.saveAll(reviews).blockLast();
    }

    @AfterEach
    public void tearDown(){
        repository.deleteAll().block();
    }

    @Test
    void addReview() {
        //given
        var request = new Review(null, 1L, "Awesome Movie",9.0);
        //when
        webTestClient.post().uri(REVIEW_URI)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedExchangeReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedExchangeReview !=null;
                    assert savedExchangeReview.getReviewId()     != null ;
                });
    }

    @Test
    void updateReview() {
        //given
        var request = new Review("2", 1L, "Awesome Movie3",9.0);

        //when
        webTestClient.put().uri(REVIEW_URI + "/{id}", "2")
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedExchangeReview = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedExchangeReview !=null;
                    assert savedExchangeReview.getMovieInfoId() != null ;
                    assertEquals(savedExchangeReview.getComment(),"Awesome Movie3");
                });
    }

    @Test
    void deleteMovieInfo() {
        webTestClient.get().uri(REVIEW_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);

        //when
        webTestClient.delete().uri(REVIEW_URI + "/{id}","2")
                .exchange()
                .expectStatus()
                .isNoContent();

        webTestClient.get().uri(REVIEW_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2);
    }

    @Test
    void getAllReviews() {
        webTestClient.get().uri(REVIEW_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void getAllReviewsByMovieInfoId() {
        var movieInfoId = 1L;

        var uri =UriComponentsBuilder.fromUriString(REVIEW_URI)
                .queryParam("movieInfoId",movieInfoId).buildAndExpand().toUri();

        webTestClient.get().uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviewList -> {
                    System.out.println("reviewList : " + reviewList);
                    assertEquals(2, reviewList.size());
                });
    }

}
