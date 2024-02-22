package com.reactivespring.moviesinfoservice.service;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class MovieInfoService {

    private MovieInfoRepository repository;

    public Mono<MovieInfo> saveMovieInfo(MovieInfo movieInfo){
        return repository.save(movieInfo);
    }

    public Mono<MovieInfo> getMovieInfo(String movieInfoId){
        return repository.findById(movieInfoId);
    }

    public Flux<MovieInfo> getAllMovieInfo(){
        return repository.findAll();
    }

    public Mono<Void> deleteMovieInfo(String movieInfoId){
        return repository.deleteById(movieInfoId);
    }

    public Mono<MovieInfo> updateMovieInfo(String id,MovieInfo updatedMovieInfo){
        return repository.findById(id)
                .flatMap(movieInfo -> {
                    movieInfo.setName(updatedMovieInfo.getName());
                    movieInfo.setYear(updatedMovieInfo.getYear());
                    movieInfo.setCast(updatedMovieInfo.getCast());
                    movieInfo.setRelease_date(updatedMovieInfo.getRelease_date());
                    return repository.save(movieInfo);
                });
    }

}
