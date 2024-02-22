package com.reactivespring.moviesinfoservice.repository;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntegrationTest {

    @Autowired
    MovieInfoRepository repository;

    @BeforeEach
    public void setUp(){
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        repository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    public void tearDown(){
        repository.deleteAll().block() ;
    }

    @Test
    public void findAll(){
        var moviesInfoFlux = repository.findAll().log();
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void findById(){
        var movieInfoMono = repository.findById("abc").log();
        StepVerifier.create(movieInfoMono)
               // .expectNextCount(1)
                .assertNext(movieInfo-> assertEquals("Dark Knight Rises",movieInfo.getName()))
                .verifyComplete();
    }

    @Test
    public void save(){
        MovieInfo movieInfo = new MovieInfo("123","Test",1976,null,LocalDate.now());
        var movieInfoMono = repository.save(movieInfo).log();
        StepVerifier.create(movieInfoMono)
                .assertNext(mvInfo-> {
                    assertNotNull(mvInfo.getMovieInfoId());
                    assertEquals("Test",mvInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    public void update(){
        var movieInfo = repository.findById("abc").block();
        movieInfo.setYear(1999);

        var movieInfoMono = repository.save(movieInfo).log();
        StepVerifier.create(movieInfoMono)
                // .expectNextCount(1)
                .assertNext(mvInfo-> assertEquals(1999,mvInfo.getYear()))
                .verifyComplete();
    }

    @Test
    public void delete(){
        repository.deleteById("abc").block();
        var movieInfoFlux = repository.findAll();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

}