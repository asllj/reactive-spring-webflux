package com.reactivespring.moviesinfoservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("movie_info")
public class MovieInfo {

    @MongoId
    private String movieInfoId;
    private String name;
    private Integer year;
    private List<String> cast;
    private LocalDate release_date;
}
