package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Mono<String> namesMono_Map_Filter(int stringLength){
        return Mono.just("Anna")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .log();
    }

    public Mono<List<String>> namesMono_FlatMap_Filter(int stringLength){
        return Mono.just("Anna")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitMonoString)
                .log();
    }

    public Flux<String> namesMono_FlatMapMany(int stringLength){
        return Mono.just("Anna")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }


    public Flux<String> namesFlux(){
        return Flux.fromIterable(List.of("Anna","Jonh","Sarah"))
                .log();
    }

    public Flux<String> namesFlux_immutability(){
        var namesFluxImmutability = Flux.fromIterable(List.of("Anna","Jonh","Sarah"));
        namesFluxImmutability.map(String::toUpperCase);
        return namesFluxImmutability;
    }


    public Flux<String> namesFluxMap(){
        return Flux.fromIterable(List.of("Anna","Jonh","Sarah"))
                .map(String::toUpperCase)
                .log();
    }

    public Flux<String> namesFluxFilter(int stringLength){
        return Flux.fromIterable(List.of("Anna","Jonh","Sarah"))
                .filter(s->s.length()==stringLength)
                .log();
    }

    public Flux<String> namesFluxFlatMap(int stringSize){
        return Flux.fromIterable(List.of("Anna","Jonh","Sarah","Ben"))
                .filter(s->s.length() > stringSize)
                .map(String::toUpperCase) //ANNA,JONH,SARAH
                .flatMap(this::splitString) //A,N,N,A,J,O,N,H,S,A,R,A,H
                .log();
    }

    public Flux<String> namesFluxFlatMapAsync(int stringSize){
        return Flux.fromIterable(List.of("Anna","Jonh","Sarah","Ben"))
                .filter(s->s.length() > stringSize)
                .map(String::toUpperCase) //ANNA,JONH,SARAH
                .flatMap(this::splitStringWithDelay) //A,N,N,A,J,O,N,H,S,A,R,A,H
                .log();
    }

    public Flux<String> namesFluxConcatMap(int stringSize){
        return Flux.fromIterable(List.of("Anna","Jonh","Sarah","Ben"))
                .filter(s->s.length() > stringSize)
                .map(String::toUpperCase) //ANNA,JONH,SARAH
                .concatMap(this::splitStringWithDelay) //A,N,N,A,J,O,N,H,S,A,R,A,H
                .log();
    }

    //ANNA -> Flux (A,N,N,A)
    private Flux<String> splitString(String name){
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    //ANNA -> Flux (A,N,N,A)
    private Flux<String> splitStringWithDelay(String name){
        var charArray = name.split("");
        var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }

    private Mono<List<String>> splitMonoString(String name){
        var charArray = name.split("");
        return Mono.just(List.of(charArray));
    }

    public Mono<String> nameMono(){
        return Mono.just("Alex")
                .log();
    }
    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux().subscribe(name -> System.out.println("Flux Name is: " + name));
        fluxAndMonoGeneratorService.nameMono().subscribe(name -> System.out.println("Mono Name is: " + name));

    }

    public Flux<String> namesFlux_transform(int stringSize){
        Function<Flux<String>,Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringSize);

        return Flux.fromIterable(List.of("Anna","Jonh","Sarah","Ben"))
                .transform(filterMap)
                .flatMap(this::splitString)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFlux_transform_switchEmpty(int stringSize){
        Function<Flux<String>,Flux<String>> filterMap = name ->
                name.map(String::toUpperCase)
                .filter(s -> s.length() > stringSize);

        var defaultFlux = Flux.just("default")
                .transform(filterMap);

        return Flux.fromIterable(List.of("Anna","Jonh","Sarah","Ben"))
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> explore_concat(){
        var abcFlux = Flux.just("A","B","C");
        var defflux = Flux.just("D","E","F");

        return Flux.concat(abcFlux,defflux);
    }

    public Flux<String> explore_concatWith(){
        var aMono = Mono.just("A");
        var defflux = Flux.just("D","E","F");

        return aMono.concatWith(defflux).log();
    }

    public Flux<String> explore_merge(){
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));
        var defflux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(120));

        return Flux.merge(abcFlux,defflux).log();
    }

    public Flux<String> explore_mergeWith(){
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));
        var defflux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defflux).log();
    }

    public Flux<String> explore_mergeSequential(){
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));
        var defflux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(120));

        return Flux.mergeSequential(abcFlux,defflux).log();
    }

    public Flux<String> explore_zip(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");

        return Flux.zip(abcFlux,defFlux,(first,second)-> first + second).log();
    }

    public Flux<String> explore_zip_1(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");
        var _123Flux = Flux.just(1,2,3);
        var _456Flux = Flux.just(4,5);

        return Flux.zip(abcFlux,defFlux,_123Flux,_456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log();
    }

    public Flux<String> explore_zipWith(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");

        return abcFlux.zipWith(defFlux,(first,second)-> first + second).log();
    }

}
