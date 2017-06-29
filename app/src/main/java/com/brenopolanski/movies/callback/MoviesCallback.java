package com.brenopolanski.movies.callback;

import com.brenopolanski.movies.model.Movie;

import java.util.List;

public interface MoviesCallback {

    void success(List<Movie> movies);

    void error(Exception error);

}
