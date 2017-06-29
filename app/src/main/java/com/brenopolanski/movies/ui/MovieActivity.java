package com.brenopolanski.movies.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.brenopolanski.movies.R;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieActivity extends BaseActivity {

    private MovieFragment movieFrag;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie, menu);
        menu.findItem(R.id.share)
                .setIcon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_share)
                        .color(Color.BLACK)
                        .sizeDp(24));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.share:
                if (movieFrag != null) {
                    movieFrag.shareMovie();
                }
                break;
        }
        return true;
    }

    @Override
    protected void init() {
        movieFrag = new MovieFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, movieFrag)
                .commit();
    }
}
