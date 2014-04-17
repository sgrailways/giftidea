package com.sgrailways.giftidea;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import roboguice.activity.RoboFragmentActivity;

public class IdeaActivity extends RoboFragmentActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_new_idea);
        Fragment fragment;
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("ideaId")) {
            fragment = new EditIdeaFragment();
        } else {
            fragment = new NewIdeaFragment();
        }
        fragment.setHasOptionsMenu(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.new_idea, fragment).commit();
    }
}
