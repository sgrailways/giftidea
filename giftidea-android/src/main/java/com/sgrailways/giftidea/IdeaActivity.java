package com.sgrailways.giftidea;

import android.app.Fragment;
import android.os.Bundle;
import com.sgrailways.giftidea.wiring.BaseActivity;

public class IdeaActivity extends BaseActivity {
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
        getFragmentManager().beginTransaction().replace(R.id.new_idea, fragment).commit();
    }
}
