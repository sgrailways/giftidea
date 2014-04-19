package com.sgrailways.giftidea;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import com.sgrailways.giftidea.wiring.ActivityModule;
import com.sgrailways.statham.ActionFactory;
import dagger.ObjectGraph;

import javax.inject.Inject;

public class AboutActivity extends PreferenceActivity {
    @Inject ActionFactory actionFactory;
    ObjectGraph activityGraph;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph = ObjectGraph.create(new ActivityModule());
        activityGraph.inject(this);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.about_title);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferenceFragment())
                .commit();
    }

    @Override protected void onDestroy() {
        activityGraph = null;
        super.onDestroy();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return actionFactory.create(item, this).invoke();
    }

    class PreferenceFragment extends android.preference.PreferenceFragment {
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
