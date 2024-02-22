package com.reactivespring.moviesinfoservice.repository;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo,String> {

    Flux<MovieInfo> findMovieInfoByYear(Integer year);
    Flux<MovieInfo> findMovieInfoByName(String name);
}
