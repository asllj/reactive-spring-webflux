package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.repository.ReviewReactiveRepository;
import jakarta.validation.ConstraintViolation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import jakarta.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class ReviewHandler {

    private ReviewReactiveRepository repository;

    @Autowired
    private Validator validator;

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(repository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED):: bodyValue);

    }

    private void validate(Review review) {
        var constraintViolations = validator.validate(review);
        log.info("constraintViolations: {} ",constraintViolations);
        if(!constraintViolations.isEmpty()){
            var errorMessages = constraintViolations.stream().map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));

            throw new ReviewDataException(errorMessages);
        }
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        var movieInfoId = request.queryParam("movieInfoId");

        if(movieInfoId.isPresent()){
            var reviews = repository.findByMovieInfoId(Long.valueOf(movieInfoId.get()));
            return buildReviewsResponse(reviews);
        }else{
            var reviews = repository.findAll();
            return buildReviewsResponse(reviews);
        }


        //.flatMap(review -> ServerResponse.status(HttpStatus.OK).body(review,Review.class))
    }

    private static Mono<ServerResponse> buildReviewsResponse(Flux<Review> reviews) {
        return ServerResponse.ok().body(reviews, Review.class);
    }


    public Mono<ServerResponse> updateReview(ServerRequest request) {
        String reviewId = request.pathVariable("id");
        var existingReview = repository.findById(reviewId);

        return existingReview.flatMap(review -> request.bodyToMono(Review.class)
                .map(reqReview -> {
                    review.setComment(reqReview.getComment());
                    review.setRating(reqReview.getRating());
                    return review;
                })
                .flatMap(repository::save)
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview)));

    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        var reviewId = request.pathVariable("id");
        var existingReview =  repository.findById(reviewId);
        return existingReview.flatMap(review -> repository.deleteById(reviewId))
                .then(ServerResponse.noContent().build());
    }
}
