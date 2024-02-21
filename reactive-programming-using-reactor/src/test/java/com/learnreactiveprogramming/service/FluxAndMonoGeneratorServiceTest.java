package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;


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
        var namesFluxMap = service.namesFluxMap();

        //then
        StepVerifier.create(namesFluxMap)
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

    @Test
    void namesFluxMap() {
        //given

        //when
        var namesFluxMap = service.namesFluxMap();

        //then
        StepVerifier.create(namesFluxMap)
                .expectNext("ANNA","JONH","SARAH")
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        //given

        //when
        var namesFluxImmutable = service.namesFlux_immutability();

        //then
        StepVerifier.create(namesFluxImmutable)
                .expectNext("Anna","Jonh","Sarah")
                .verifyComplete();
    }

    @Test
    void namesFluxFilter() {
        //given
        int stringSize=4;
        //when
        var namesFluxImmutable = service.namesFluxFilter(stringSize);

        //then
        StepVerifier.create(namesFluxImmutable)
                .expectNext("Anna","Jonh")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMap() {
        //given
        int stringSize=3;
        //when
        var namesFluxFlatMap = service.namesFluxFlatMap(stringSize);

        //then
        StepVerifier.create(namesFluxFlatMap)
                .expectNext("A","N","N","A","J","O","N","H","S","A","R","A","H")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapAsync() {
        //given
        int stringSize=3;
        //when
        var namesFluxFlatMap = service.namesFluxFlatMapAsync(stringSize);

        //then
        StepVerifier.create(namesFluxFlatMap)
                .expectNextCount(13)
                .verifyComplete();

    }

    @Test
    void namesFluxConcatMap() {
        //given
        int stringSize=3;
        //when
        var namesFluxFlatMap = service.namesFluxConcatMap(stringSize);

        //then
        StepVerifier.create(namesFluxFlatMap)
                .expectNext("A","N","N","A","J","O","N","H","S","A","R","A","H")
                .verifyComplete();
    }

    @Test
    void namesMono_FlatMap_Filter() {
        //given
        int stringSize=3;
        //when
        var namesMonoFlatMap = service.namesMono_FlatMap_Filter(stringSize);

        //then
        StepVerifier.create(namesMonoFlatMap)
                .expectNext(List.of("A","N","N","A"))
                .verifyComplete();
    }

    @Test
    void namesMono_FlatMapMany() {
        //given
        int stringSize=3;
        //when
        var namesMonoFlatMapMany = service.namesMono_FlatMapMany(stringSize);

        //then
        StepVerifier.create(namesMonoFlatMapMany)
                .expectNext("A","N","N","A")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {

        //given
        int stringSize=3;
        //when
        var namesFluxTransform = service.namesFlux_transform(stringSize);

        //then
        StepVerifier.create(namesFluxTransform)
                .expectNext("A","N","N","A","J","O","N","H","S","A","R","A","H")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform1() {

        //given
        int stringSize=6;
        //when
        var namesFluxTransform = service.namesFlux_transform(stringSize);

        //then
        StepVerifier.create(namesFluxTransform)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchEmpty() {
        //given
        int stringSize=6;
        //when
        var namesFluxTransformSwitchEmpty = service.namesFlux_transform_switchEmpty(stringSize);

        //then
        StepVerifier.create(namesFluxTransformSwitchEmpty)
                .expectNext("D","E","F","A","U","L","T")
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        var explore_concat = service.explore_concat();

        StepVerifier.create(explore_concat)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void explore_concatWith() {
        var explore_concatWith = service.explore_concatWith();

        StepVerifier.create(explore_concatWith)
                .expectNext("A","D","E","F")
                .verifyComplete();
    }

    @Test
    void explore_merge() {
        var explore_merge = service.explore_merge();

        StepVerifier.create(explore_merge)
                .expectNext("A","D","B","E","C","F")
                .verifyComplete();
    }

    @Test
    void explore_mergeWith() {
        var explore_mergeWith = service.explore_mergeWith();

        StepVerifier.create(explore_mergeWith)
                .expectNext("A","D","B","E","C","F")
                .verifyComplete();
    }


    @Test
    void explore_mergeSequential() {
        var explore_mergeSequential = service.explore_mergeSequential();

        StepVerifier.create(explore_mergeSequential)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void explore_zip() {
        var explore_zip = service.explore_zip();

        StepVerifier.create(explore_zip)
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }

    @Test
    void explore_zip_1() {
        var explore_zip_1 = service.explore_zip_1();

        StepVerifier.create(explore_zip_1)
                .expectNext("AD14","BE25","CF36")
                .verifyComplete();
    }

    @Test
    void explore_zipWith() {
        var explore_zipWith = service.explore_zipWith();

        StepVerifier.create(explore_zipWith)
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }
}