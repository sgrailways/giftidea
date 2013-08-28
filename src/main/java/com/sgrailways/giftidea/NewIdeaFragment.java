package com.sgrailways.giftidea;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sgrailways.R;
import roboguice.fragment.RoboFragment;

public class NewIdeaFragment extends RoboFragment {
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_idea, container, false);
    }
}
