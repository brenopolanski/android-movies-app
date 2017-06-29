package com.brenopolanski.movies.model;

import org.joda.time.DateTime;

import java.io.Serializable;

public class Movie implements Serializable {
    private int id;
    private String title;
    private String overview;
    private String posterUrl;
    private String backdropUrl;
    private String trailerUrl;
    private DateTime releaseDate;
    private float rating;
    private boolean adult;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getOverview() {

        return overview;
    }

    public void setOverview(String overview) {

        this.overview = overview;
    }

    public String getPosterUrl() {

        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {

        this.posterUrl = posterUrl;
    }

    public String getBackdropUrl() {

        return backdropUrl;
    }

    public void setBackdropUrl(String backdropUrl) {

        this.backdropUrl = backdropUrl;
    }

    public String getTrailerUrl() {

        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {

        this.trailerUrl = trailerUrl;
    }

    public DateTime getReleaseDate() {

        return releaseDate;
    }

    public void setReleaseDate(DateTime releaseDate) {

        this.releaseDate = releaseDate;
    }

    public float getRating() {

        return rating;
    }

    public void setRating(float rating) {

        this.rating = rating;
    }

    public boolean isAdult() {

        return adult;
    }

    public void setAdult(boolean adult) {

        this.adult = adult;
    }
}
