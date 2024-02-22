package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MovieInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class MovieInfoController {

    private MovieInfoService movieInfoService;

    @PostMapping(value = "/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> createMovieInfo(@RequestBody MovieInfo movieInfo){
        return movieInfoService.saveMovieInfo(movieInfo).log();
    }

    @GetMapping(value = "/movieinfos/{movieInfoId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<MovieInfo> getMovieInfo(@PathVariable String movieInfoId){
        return movieInfoService.getMovieInfo(movieInfoId).log();
    }

    @GetMapping(value = "/movieinfos")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMovieInfo(){
        return movieInfoService.getAllMovieInfo().log();
    }

    @PutMapping(value = "/movieinfos/{movieInfoId}")
    public Mono<MovieInfo> updateMovieInfo(@PathVariable String movieInfoId,@RequestBody MovieInfo movieInfo){
        return movieInfoService.updateMovieInfo(movieInfoId,movieInfo).log();
    }

    @DeleteMapping(value = "/movieinfos/{movieInfoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String movieInfoId){
        return movieInfoService.deleteMovieInfo(movieInfoId).log();
    }
}
