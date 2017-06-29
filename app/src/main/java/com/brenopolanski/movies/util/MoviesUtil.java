package com.brenopolanski.movies.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.brenopolanski.movies.R;
import com.brenopolanski.movies.callback.MoviesCallback;
import com.brenopolanski.movies.callback.ReviewsCallback;
import com.brenopolanski.movies.model.Movie;
import com.brenopolanski.movies.model.Review;
import com.brenopolanski.movies.provider.MovieContract;
import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoviesUtil {
    private static final Webb WEBB = Webb.create();

    private static final String TMDB_API_MOVIES_URL = "http://api.themoviedb.org/3/movie/%s?api_key=%s&page=%s";
    private static final String TMDB_API_VIDEOS_URL = "http://api.themoviedb.org/3/movie/%s/videos?api_key=%s";
    private static final String TMDB_API_REVIEWS_URL = "http://api.themoviedb.org/3/movie/%s/reviews?api_key=%s";
    private static final String TMDB_POSTER_URL = "https://image.tmdb.org/t/p/w185%s";
    private static final String TMDB_BACKDROP_URL = "https://image.tmdb.org/t/p/w300%s";

    private static final String TYPE_POPULAR = "popular";
    private static final String TYPE_TOP_RATED = "top_rated";
    private static final String TYPE_FAVORITES = "favorites";

    public static boolean isFavorite(Context context, Movie movie) {
        Cursor cursor = context.getContentResolver()
                .query(MovieContract.CONTENT_URI,
                        null,
                        String.format("%s = ? and %s = ?", MovieContract.MOVIE_ID, MovieContract.TYPE),
                        new String[]{movie.getId() + "", TYPE_FAVORITES},
                        null
                );
        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        return isFavorite;
    }

    public static boolean toggleFavorite(Context context, Movie movie) {
        if (isFavorite(context, movie)) {
            deleteMovie(context, TYPE_FAVORITES, movie);
            return false;
        } else {
            saveMovie(context, TYPE_FAVORITES, movie);
            return true;
        }
    }

    public static void getPopularMovies(Activity activity, MoviesCallback callback) {
        getMovies(activity, TYPE_POPULAR, callback);
    }

    public static void getTopRatedMovies(Activity activity, MoviesCallback callback) {
        getMovies(activity, TYPE_TOP_RATED, callback);
    }

    public static void getFavoritesMovies(Activity activity, MoviesCallback callback) {
        getMovies(activity, TYPE_FAVORITES, callback);
    }

    private static void getMovies(final Activity activity, final String type, final MoviesCallback callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (Util.isConnected(activity, false) && !type.equals(TYPE_FAVORITES)) {
                    getMoviesFromApi(activity, type);
                }
                getMoviesFromDb(activity, type, callback);
            }
        });
    }

    private static void getMoviesFromApi(Activity activity, String type) {
        String apiUrl = String.format(TMDB_API_MOVIES_URL, type, activity.getString(R.string.tmdb_api_key), 1);
        try {
            JSONArray moviesJson = WEBB.get(apiUrl)
                    .asJsonObject()
                    .getBody()
                    .getJSONArray("results");
            List<Movie> movies = toMovies(activity, moviesJson);
            deleteMovies(activity, type);
            saveMovies(activity, type, movies);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getMoviesFromDb(Activity activity, String type, final MoviesCallback callback) {
        try {
            Cursor cursor = activity.getContentResolver()
                    .query(MovieContract.CONTENT_URI,
                            null,
                            MovieContract.TYPE + " = ?",
                            new String[]{type},
                            null
                    );
            final List<Movie> movies = toMovies(cursor);
            cursor.close();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.success(movies);
                }
            });
        } catch (final Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.error(e);
                }
            });
        }
    }

    public static void getReviewsFromApi(final Activity activity, final Movie movie, final ReviewsCallback callback) {
        if (Util.isConnected(activity, false)) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    String apiUrl = String.format(TMDB_API_REVIEWS_URL, movie.getId(), activity.getString(R.string.tmdb_api_key));
                    final List<Review> reviews = new ArrayList<>();
                    try {
                        JSONArray reviewsJson = WEBB.get(apiUrl)
                                .asJsonObject()
                                .getBody()
                                .getJSONArray("results");
                        reviews.addAll(toReviews(reviewsJson));
                    } catch (final Exception e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.error(e);
                            }
                        });
                    }
                    if (reviews.isEmpty()) {
                        Review review = new Review();
                        review.setContent(activity.getString(R.string.no_review_found));
                        reviews.add(review);
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.success(reviews);
                        }
                    });
                }
            });
        } else {
            Review review = new Review();
            review.setContent(activity.getString(R.string.conn_internet));
            final List<Review> reviews = new ArrayList<>();
            reviews.add(review);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.success(reviews);
                }
            });
        }
    }

    private static void saveMovie(final Context context, final String type, final Movie movie) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Movie> movies = new ArrayList<>();
                movies.add(movie);
                saveMovies(context, type, movies);
            }
        });
    }

    private static void saveMovies(Context context, String type, List<Movie> movies) {
        if (movies != null) {
            ContentValues[] moviesValues = new ContentValues[movies.size()];
            for (int i = 0; i < movies.size(); i++) {
                try {
                    Movie movie = movies.get(i);
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MOVIE_ID, movie.getId());
                    movieValues.put(MovieContract.TYPE, type);
                    movieValues.put(MovieContract.TITLE, movie.getTitle());
                    movieValues.put(MovieContract.OVERVIEW, movie.getOverview());
                    movieValues.put(MovieContract.POSTER_URL, movie.getPosterUrl());
                    movieValues.put(MovieContract.BACKDROP_URL, movie.getBackdropUrl());
                    movieValues.put(MovieContract.TRAILER_URL, movie.getTrailerUrl());
                    movieValues.put(MovieContract.RELEASE_DATE, Util.toDbDate(movie.getReleaseDate()));
                    movieValues.put(MovieContract.RATING, movie.getRating());
                    movieValues.put(MovieContract.ADULT, movie.isAdult() ? 1 : 0);
                    moviesValues[i] = movieValues;
                } catch (Exception ignore) {
                }
            }
            context.getContentResolver()
                    .bulkInsert(MovieContract.CONTENT_URI, moviesValues);
        }
    }

    private static void deleteMovie(final Context context, final String type, final Movie movie) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                context.getContentResolver()
                        .delete(MovieContract.CONTENT_URI,
                                MovieContract.MOVIE_ID + " = ? and " + MovieContract.TYPE + " = ?",
                                new String[]{movie.getId() + "", type});
            }
        });
    }

    private static void deleteMovies(final Context context, final String type) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                context.getContentResolver()
                        .delete(MovieContract.CONTENT_URI,
                                MovieContract.TYPE + " = ?",
                                new String[]{type});
            }
        });
    }

    private static List<Movie> toMovies(Cursor cursor) {
        List<Movie> movies = new ArrayList<>();
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.setId(cursor.getInt(
                    cursor.getColumnIndex(MovieContract.MOVIE_ID)));
            movie.setTitle(cursor.getString(
                    cursor.getColumnIndex(MovieContract.TITLE)));
            movie.setOverview(cursor.getString(
                    cursor.getColumnIndex(MovieContract.OVERVIEW)));
            movie.setPosterUrl(cursor.getString(
                    cursor.getColumnIndex(MovieContract.POSTER_URL)));
            movie.setBackdropUrl(cursor.getString(
                    cursor.getColumnIndex(MovieContract.BACKDROP_URL)));
            movie.setTrailerUrl(cursor.getString(
                    cursor.getColumnIndex(MovieContract.TRAILER_URL)));
            movie.setReleaseDate(Util.toDate(cursor.getString(
                    cursor.getColumnIndex(MovieContract.RELEASE_DATE))));
            movie.setRating(cursor.getFloat(
                    cursor.getColumnIndex(MovieContract.RATING)));
            movie.setAdult(cursor.getInt(
                    cursor.getColumnIndex(MovieContract.ADULT)) == 1);
            movies.add(movie);
        }
        return movies;
    }

    private static List<Movie> toMovies(Context context, JSONArray jsonMovies) {
        List<Movie> movies = new ArrayList<>();
        if (jsonMovies != null) {
            for (int i = 0; i < jsonMovies.length(); i++) {
                try {
                    JSONObject jsonMovie = jsonMovies.getJSONObject(i);
                    int movieId = jsonMovie.getInt("id");
                    Movie movie = new Movie();
                    movie.setId(movieId);
                    movie.setTitle(jsonMovie.getString("title"));
                    movie.setOverview(jsonMovie.getString("overview"));
                    movie.setPosterUrl(String.format(TMDB_POSTER_URL, jsonMovie.getString("poster_path")));
                    movie.setBackdropUrl(String.format(TMDB_BACKDROP_URL, jsonMovie.getString("backdrop_path")));
                    movie.setTrailerUrl(getTrailerUrl(context, movieId));
                    movie.setReleaseDate(Util.toDate(jsonMovie.getString("release_date")));
                    movie.setRating((float) jsonMovie.getDouble("vote_average"));
                    movie.setAdult(jsonMovie.getBoolean("adult"));
                    movies.add(movie);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return movies;
    }

    private static List<Review> toReviews(JSONArray jsonReviews) {
        List<Review> reviews = new ArrayList<>();
        if (jsonReviews != null) {
            for (int i = 0; i < jsonReviews.length(); i++) {
                try {
                    JSONObject jsonReview = jsonReviews.getJSONObject(i);
                    Review review = new Review();
                    review.setAuthor(jsonReview.getString("author"));
                    review.setContent(jsonReview.getString("content"));
                    reviews.add(review);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return reviews;
    }

    private static String getTrailerUrl(Context context, int movieId) {
        String apiUrl = String.format(TMDB_API_VIDEOS_URL, movieId, context.getString(R.string.tmdb_api_key));
        try {
            JSONArray trailersJson = WEBB.get(apiUrl)
                    .asJsonObject()
                    .getBody()
                    .getJSONArray("results");
            for (int i = 0; i < trailersJson.length(); i++) {
                JSONObject trailerJson = trailersJson.getJSONObject(i);
                if (trailerJson.getString("site").toLowerCase().equals("youtube")) {
                    return "https://youtube.com/watch?v=" + trailerJson.getString("key");
                }
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
