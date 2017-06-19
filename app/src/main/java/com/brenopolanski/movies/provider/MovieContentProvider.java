package com.brenopolanski.movies.provider;

import com.tjeannin.provigen.ProviGenProvider;

/**
 * Created by brenopolanski on 16/06/17.
 */

public class MovieContentProvider extends ProviGenProvider {

    private static final String DB_NAME     = "movies";
    private static final Class[] CONTRACTS  = new Class[]{
            MovieContract.class
    };

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(), DB_NAME, null, 1, CONTRACTS);
    }

    @Override
    public Class[] contractClasses() {
        return CONTRACTS;
    }
}
