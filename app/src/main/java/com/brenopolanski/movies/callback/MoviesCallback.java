package com.brenopolanski.movies.callback;

import com.brenopolanski.movies.model.Movie;

import java.util.List;

/**
 * Created by brenopolanski on 16/06/17.
 */

public interface MoviesCallback {
    void success(List<Movie> movies);

    void error(Exception error);
}
