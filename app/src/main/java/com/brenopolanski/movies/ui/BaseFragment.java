package com.brenopolanski.movies.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import butterknife.Unbinder;
import icepick.Icepick;

public abstract class BaseFragment extends Fragment {
    protected Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected abstract void init();
}
