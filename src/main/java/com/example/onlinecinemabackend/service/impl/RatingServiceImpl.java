package com.example.onlinecinemabackend.service.impl;

import com.example.onlinecinemabackend.entity.Film;
import com.example.onlinecinemabackend.entity.Rating;

import com.example.onlinecinemabackend.entity.Series;
import com.example.onlinecinemabackend.entity.User;
import com.example.onlinecinemabackend.repository.FilmRepository;
import com.example.onlinecinemabackend.repository.RatingRepository;
import com.example.onlinecinemabackend.repository.SeriesRepository;
import com.example.onlinecinemabackend.service.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RatingServiceImpl extends AbstractEntityService<Rating, UUID, RatingRepository> implements RatingService {


    private final FilmService filmService;

    private final SeriesService seriesService;

    private final UserService userService;

    public RatingServiceImpl(RatingRepository repository, FilmRepository filmRepository, SeriesRepository seriesRepository, FilmService filmService, SeriesService seriesService, UserService userService) {
        super(repository);
        this.filmService = filmService;
        this.seriesService = seriesService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Rating addRating(Rating rating, UUID userId, UUID filmId, UUID seriesId) {
        User author = userService.findById(userId);
        Film film = null;
        Series series = null;
        if (filmId != null)
            film = filmService.findById(filmId);
        if (seriesId != null)
            series = seriesService.findById(seriesId);
        author.addRating(rating);
        if (film != null) {
            film.addRating(rating);
            filmService.update(filmId,film);
        }
        if (series != null){
            series.addRating(rating);
            seriesService.update(seriesId,series);
        }
        userService.update(userId,author);
        return save(rating);
    }

    @Override
    public Rating save(Rating entity){
        log.info("Save entity: {}", entity);
        if (entity.getFilm()!=null) {
            Film updateFilm = filmService.findById(entity.getFilm().getId());
            List<Rating> ratings = updateFilm.getRatings();
            double ratingSum = 0;
            for (var rating : ratings) {
                ratingSum += rating.getRating();
            }
            updateFilm.setAverage_rating(ratingSum / ratings.size());
            filmService.update(entity.getFilm().getId(), updateFilm);
        }
        if (entity.getSeries()!=null) {
            Series updateSeries = seriesService.findById(entity.getSeries().getId());
            List<Rating> ratings = updateSeries.getRatings();
            double ratingSum = 0;
            for(var rating : ratings){
                ratingSum+= rating.getRating();
            }
            updateSeries.setAverage_rating(ratingSum/ratings.size());
            seriesService.update(entity.getSeries().getId(),updateSeries);
        }

        return repository.save(entity);
    }

    @Override
    protected Rating updateFields(Rating oldEntity, Rating newEntity) {

        if (StringUtils.hasText(newEntity.getComment())){
            oldEntity.setComment(newEntity.getComment());
        }
        if (newEntity.getRating() >= 1 && newEntity.getRating() <= 10){
            oldEntity.setRating(newEntity.getRating());
        }
        if (newEntity.getFilm()!=null){
            oldEntity.setFilm(newEntity.getFilm());
            Film updateFilm = filmService.findById(newEntity.getFilm().getId());
            List<Rating> ratings = updateFilm.getRatings();
            double ratingSum = 0;
            for(var rating : ratings){
                ratingSum+= rating.getRating();
            }
            updateFilm.setAverage_rating(ratingSum/ratings.size());
            filmService.update(newEntity.getFilm().getId(),updateFilm);
        }
        if (newEntity.getUser()!=null){
            oldEntity.setUser(newEntity.getUser());
        }
        if (newEntity.getSeries()!=null){
            oldEntity.setSeries(newEntity.getSeries());
            Series updateSeries = seriesService.findById(newEntity.getSeries().getId());
            List<Rating> ratings = updateSeries.getRatings();
            double ratingSum = 0;
            for(var rating : ratings){
                ratingSum+= rating.getRating();
            }
            updateSeries.setAverage_rating(ratingSum/ratings.size());
            seriesService.update(newEntity.getSeries().getId(),updateSeries);
        }
        return oldEntity;
    }

    @Override
    public Page<Rating> findAllByRating(Integer rating, Pageable pageable) {
        return repository.findAllByRating(rating,pageable);
    }

    @Override
    public Page<Rating> findAllByUser_Id(UUID userId, Pageable pageable) {
        return repository.findAllByUser_Id(userId,pageable);
    }

    @Override
    public Page<Rating> findAllByFilm_Id(UUID filmId, Pageable pageable) {
        return repository.findAllByFilm_Id(filmId, pageable);
    }

    @Override
    public Page<Rating> findAllBySeries_Id(UUID seriesId, Pageable pageable) {
        return repository.findAllBySeries_Id(seriesId,pageable);
    }
}
