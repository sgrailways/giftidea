package com.sgrailways.giftidea;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectExtra;

public class IdeaActivity extends RoboFragmentActivity {
    @InjectExtra(value = "ideaId", optional = true) Long ideaId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_new_idea);
        Fragment fragment;
        if(ideaId != null) {
            fragment = new EditIdeaFragment();
        } else {
            fragment = new NewIdeaFragment();
        }
        fragment.setHasOptionsMenu(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.new_idea, fragment).commit();
    }
}
