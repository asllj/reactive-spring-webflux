package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MovieInfoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
@Slf4j
public class MovieInfoController {

    private MovieInfoService movieInfoService;

    @PostMapping(value = "/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> createMovieInfo(@RequestBody @Valid MovieInfo movieInfo){
        return movieInfoService.saveMovieInfo(movieInfo).log();
    }

    @GetMapping(value = "/movieinfos/{movieInfoId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<MovieInfo>> getMovieInfo(@PathVariable String movieInfoId){
        return movieInfoService.getMovieInfo(movieInfoId)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @GetMapping(value = "/movieinfos")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMovieInfo(@RequestParam(name = "year",required = false) Integer year,
                                           @RequestParam(name = "name",required = false) String name){
        log.info("Year is: {}",year);
        if(year!=null)
            return  movieInfoService.getAllMovieInfoByYear(year);
        if(name!=null)
            return  movieInfoService.getAllMovieInfoByName(name);
        return movieInfoService.getAllMovieInfo().log();
    }

    @PutMapping(value = "/movieinfos/{movieInfoId}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String movieInfoId, @RequestBody MovieInfo movieInfo){
        return movieInfoService.updateMovieInfo(movieInfoId,movieInfo)
                .map( ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @DeleteMapping(value = "/movieinfos/{movieInfoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String movieInfoId){
        return movieInfoService.deleteMovieInfo(movieInfoId).log();
    }
}
