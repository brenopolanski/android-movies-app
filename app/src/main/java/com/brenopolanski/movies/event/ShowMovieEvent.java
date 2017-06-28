package com.brenopolanski.movies.event;

import android.graphics.Movie;

/**
 * Created by brenopolanski on 16/06/17.
 */

public class ShowMovieEvent {
    public final Movie movie;

    public ShowMovieEvent(Movie movie) {

        this.movie = movie;
    }
}
