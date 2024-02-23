package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;


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
        return webClient.get()
                .uri(url,movieInfoId)
                .retrieve()
                .bodyToMono(MovieInfo.class)
                .log();
    }
}
