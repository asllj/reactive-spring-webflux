package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewsRestClient {

    private WebClient webClient;

    private String reviewsUrl;

    public ReviewsRestClient(WebClient webClient,@Value("${restClient.reviewsUrl}")String reviewsUrl){
        this.webClient=webClient;
        this.reviewsUrl=reviewsUrl;
    }

    public Flux<Review> retrieveReviews(String movieId){
        var url=UriComponentsBuilder.fromHttpUrl(reviewsUrl)
                .queryParam("movieInfoId",movieId)
                .buildAndExpand().toUriString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (clientResponse -> {
                    log.info("Status code : {}", clientResponse.statusCode().value());
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new ReviewsClientException(response)));
                }))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.info("Status code : {}", clientResponse.statusCode().value());

                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new ReviewsServerException("Server exception in ReviewsService " + response)));
                })
                .bodyToFlux(Review.class)
                .log();
    }
}
