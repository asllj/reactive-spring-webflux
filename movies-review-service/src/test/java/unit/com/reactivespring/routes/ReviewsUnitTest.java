package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalExceptionHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ReviewHandler.class, ReviewRouter.class, GlobalExceptionHandler.class})
@WebFluxTest
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    public static final String REVIEW_URI = "/v1/reviews";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReviewReactiveRepository repository;

    @Test
    void addReview() {
        var request = new Review(null, 1L, "Awesome Movie",9.0);

        //when
        when(repository.save(isA(Review.class))).thenReturn(Mono.just(new Review("1", 1L, "Awesome Movie",9.0)));

        webTestClient.post().uri(REVIEW_URI)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedExchangeReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedExchangeReview !=null;
                    assert savedExchangeReview.getReviewId()!= null ;
                });
    }

    @Test
    void addReview_validation() {
        var request = new Review(null, null, "Awesome Movie",-9.0);

        //when
        when(repository.save(isA(Review.class))).thenReturn(Mono.just(new Review("1", 1L, "Awesome Movie",9.0)));

        webTestClient.post().uri(REVIEW_URI)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("review.movieInfoId must not be null,review.rating.negative : please pass a non-negative value");

    }
    @Test
    void updateReview() {
        //given
        var request = new Review("1", 1L, "Awesome Movie3",9.0);

        when(repository.findById(isA(String.class))).thenReturn(Mono.just(new Review("1", 1L, "Awesome Movie",9.0)));
        when(repository.save(isA(Review.class))).thenReturn(Mono.just(new Review("1", 1L, "Awesome Movie3",9.0)));
        //when
        webTestClient.put().uri(REVIEW_URI + "/{id}", "1")
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
    void deleteReview() {
        //given
        var reviewId= "abc";
        when(repository.findById((String) any())).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
        when(repository.deleteById((String) any())).thenReturn(Mono.empty());

        //when
        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", reviewId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getAllReviews() {

        when(repository.findAll()).thenReturn(Flux.just(new Review(null, 1L, "Awesome Movie",9.0),
                new Review("2", 1L, "Awesome Movie1",9.0),
                new Review(null, 2L, "Excellent Movie",8.0)));

        webTestClient.get().uri(REVIEW_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }
}
