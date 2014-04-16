package com.sgrailways.giftidea;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.inject.Inject;
import com.sgrailways.statham.ActionFactory;

import roboguice.activity.RoboPreferenceActivity;

public class AboutActivity extends RoboPreferenceActivity {
    @Inject ActionFactory actionFactory;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.about_title);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferenceFragment())
                .commit();
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
