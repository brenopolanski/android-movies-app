package com.brenopolanski.movies.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brenopolanski.movies.R;
import com.brenopolanski.movies.callback.ReviewsCallback;
import com.brenopolanski.movies.event.ShowMovieEvent;
import com.brenopolanski.movies.event.UpdateFavoritesEvent;
import com.brenopolanski.movies.model.Movie;
import com.brenopolanski.movies.model.Review;
import com.brenopolanski.movies.ui.adapter.ReviewsAdapter;
import com.brenopolanski.movies.util.MoviesUtil;
import com.brenopolanski.movies.util.Util;
import com.bumptech.glide.Glide;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;

public class MovieFragment extends BaseFragment {

    @State
    Movie movie;

    @BindView(R.id.backdrop)
    ImageView backdropView;
    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.release_date)
    TextView releaseDateView;
    @BindView(R.id.rating)
    TextView ratingView;
    @BindView(R.id.overview)
    TextView overviewView;
    @BindView(R.id.reviews)
    RecyclerView reviewsView;
    @BindView(R.id.favorite)
    FloatingActionButton favoriteView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (movie != null) {
            init();
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true)
    public void onEvent(ShowMovieEvent event) {
        movie = event.movie;
        init();
    }

    @OnClick(R.id.trailer)
    public void playTrailer() {
        Util.openLinkInExternalApp(getContext(), movie.getTrailerUrl());
    }

    @OnClick(R.id.favorite)
    public void toggleFavorite() {
        boolean isFavorite = MoviesUtil.toggleFavorite(getContext(), movie);
        updateFavoriteFab(isFavorite);
        EventBus.getDefault().postSticky(new UpdateFavoritesEvent());
    }

    @Override
    protected void init() {
        Glide.with(getContext())
                .load(movie.getBackdropUrl())
                .into(backdropView);
        titleView.setText(movie.getTitle());
        releaseDateView.setText(Util.toPrettyDate(movie.getReleaseDate()));
        ratingView.setText(movie.getRating() + "");
        overviewView.setText(movie.getOverview());
        reviewsView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewsView.setHasFixedSize(false);
        updateFavoriteFab(MoviesUtil.isFavorite(getContext(), movie));
        loadReviews();
    }

    private void updateFavoriteFab(boolean isFavorite) {
        GoogleMaterial.Icon favoriteIcon = isFavorite ?
                GoogleMaterial.Icon.gmd_favorite : GoogleMaterial.Icon.gmd_favorite_border;
        favoriteView.setImageDrawable(new IconicsDrawable(getContext())
                .icon(favoriteIcon)
                .color(Color.WHITE)
                .sizeDp(48));
    }

    public void shareMovie() {
        String text = String.format("%s\n%s", movie.getTitle(), movie.getTrailerUrl());
        Util.shareText(getActivity(), text);
    }

    private void loadReviews() {
        MoviesUtil.getReviewsFromApi(getActivity(), movie, new ReviewsCallback() {
            @Override
            public void success(List<Review> reviews) {
                if (reviewsView != null) {
                    reviewsView.setAdapter(new ReviewsAdapter(getContext(), reviews));
                }
            }

            @Override
            public void error(Exception error) {
                error.printStackTrace();
            }
        });
    }

}
