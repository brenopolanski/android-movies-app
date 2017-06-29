package com.brenopolanski.movies.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.brenopolanski.movies.R;
import com.brenopolanski.movies.event.TwoPaneEvent;
import com.brenopolanski.movies.util.Util;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    boolean twoPane;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.movies)
    ViewPager moviesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Util.setupToolbar(this, toolbar);

        if (findViewById(R.id.movie_detail) != null) {
            twoPane = true;
        }

        init();
    }

    @Override
    protected void init() {
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        moviesView.setOffscreenPageLimit(2);
        moviesView.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(moviesView);
        EventBus.getDefault().postSticky(new TwoPaneEvent(twoPane));
    }

    public class TabAdapter extends FragmentPagerAdapter {

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MoviesFragment.newInstance(MoviesFragment.Type.POPULAR, twoPane);
                case 1:
                    return MoviesFragment.newInstance(MoviesFragment.Type.TOP_RATED, twoPane);
                case 2:
                    return MoviesFragment.newInstance(MoviesFragment.Type.FAVORITES, twoPane);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.popular);
                case 1:
                    return getString(R.string.top_rated);
                case 2:
                    return getString(R.string.favorites);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
