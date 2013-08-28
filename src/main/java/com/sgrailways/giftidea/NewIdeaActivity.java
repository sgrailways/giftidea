package com.sgrailways.giftidea;

import android.os.Bundle;
import com.sgrailways.R;
import roboguice.activity.RoboFragmentActivity;

public class NewIdeaActivity extends RoboFragmentActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_idea);
        NewIdeaFragment fragment = new NewIdeaFragment();
        fragment.setHasOptionsMenu(true);
        getSupportFragmentManager().beginTransaction().add(R.id.new_idea, fragment).commit();
    }
}
