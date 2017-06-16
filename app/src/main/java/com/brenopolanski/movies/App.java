package com.brenopolanski.movies;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by brenopolanski on 15/06/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}

