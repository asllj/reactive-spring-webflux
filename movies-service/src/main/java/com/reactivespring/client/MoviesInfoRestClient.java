package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetrySpec;

import java.time.Duration;


@Component
@Slf4j
public class MoviesInfoRestClient {

    private WebClient webClient;


    private String moviesInfoUrl;

    public MoviesInfoRestClient(WebClient webClient,@Value("${restClient.moviesInfoUrl}")String moviesInfoUrl){
        this.webClient=webClient;
        this.moviesInfoUrl=moviesInfoUrl;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieInfoId){
        var url = moviesInfoUrl.concat("/{id}");

        var retrySpec = RetrySpec.fixedDelay(3, Duration.ofSeconds(1))
                .filter((ex) -> ex instanceof MoviesInfoServerException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));

        return webClient.get()
                .uri(url,movieInfoId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (clientResponse -> {
                    log.info("Status code : {}", clientResponse.statusCode().value());
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MoviesInfoClientException("There is no MovieInfo available for the passed in Id : " + movieInfoId, clientResponse.statusCode().value()));
                     }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new MoviesInfoClientException(response, clientResponse.statusCode().value())));
                }))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.info("Status code : {}", clientResponse.statusCode().value());

                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new MoviesInfoServerException("Server exception in MoviesInfoService " + response)));
                })
                .bodyToMono(MovieInfo.class)
                .retryWhen(retrySpec)
                .log();
    }
}
