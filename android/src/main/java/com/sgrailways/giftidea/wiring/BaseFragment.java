package com.sgrailways.giftidea.wiring;

import android.app.Fragment;
import android.os.Bundle;

public class BaseFragment extends Fragment {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity activity = (BaseActivity)getActivity();
        activity.inject(this);
    }
}
