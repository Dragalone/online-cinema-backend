package com.example.onlinecinemabackend.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmResponse {

    private UUID id;

    private String title;

    private String description;

    private Date release_date;

    private BigDecimal cost;

    private String resource_link;

    private List<GenreResponse> genres = new ArrayList<>();

    private List<ActorResponse> actors = new ArrayList<>();

    private List<RatingResponse> ratings = new ArrayList<>();
}
