package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
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
                .bodyToFlux(Review.class)
                .log();
    }
}
