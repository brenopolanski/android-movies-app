package com.brenopolanski.movies.callback;

import com.brenopolanski.movies.model.Review;

import java.util.List;

public interface ReviewsCallback {

    void success(List<Review> reviews);

    void error(Exception error);

}
