package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

    @Test
    public void testNamesFlux_validateAllElements(){
        //given

        //when
        var namesFlux = service.namesFlux();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("Anna","Jonh","Sarah")
                .verifyComplete();
    }

    @Test
    public void testNamesFlux_expect3Elements(){
        //given

        //when
        var namesFlux = service.namesFlux();

        //then
        StepVerifier.create(namesFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void testNamesFlux_validateFirstElementAndexpectMore2Elements(){
        //given

        //when
        var namesFlux = service.namesFlux();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("Anna")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void testNamesMono_validateElement(){
        //given

        //when
        var nameMono = service.nameMono();

        //then
        StepVerifier.create(nameMono)
                .expectNext("Alex")
                .verifyComplete();
    }
}