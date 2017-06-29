package com.brenopolanski.movies.event;

import com.brenopolanski.movies.model.Movie;

public class ShowMovieEvent {
    public final Movie movie;

    public ShowMovieEvent(Movie movie) {

        this.movie = movie;
    }
}
