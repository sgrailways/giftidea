package com.sgrailways.giftidea;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.sgrailways.giftidea.wiring.BaseActivity;
import com.sgrailways.statham.ActionFactory;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
    @Inject ActionFactory actionFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.recipients, new RecipientsList())
                .replace(R.id.upcoming, new UpcomingEventsFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.sgrailways.giftidea.R.menu.main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return actionFactory.create(item, this).invoke();
    }

    @Override protected void onResume() {
        getFragmentManager().beginTransaction().replace(R.id.recipients, new RecipientsList()).commit();
        super.onResume();
    }
}

