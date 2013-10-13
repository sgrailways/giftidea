package com.sgrailways.giftidea;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.inject.Inject;
import com.sgrailways.giftidea.events.RefreshIdeasListEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.activity.RoboFragmentActivity;

public class RecipientIdeasActivity extends RoboFragmentActivity {
    @Inject Bus bus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.recipients, new RecipientIdeasList()).commit();
        bus.register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                finish();
                startActivity(new Intent(this, IdeaActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onResume() {
        getSupportFragmentManager().beginTransaction().replace(R.id.recipients, new RecipientIdeasList()).commit();
        super.onResume();
    }

    @Subscribe public void answerRefreshIdeasList(RefreshIdeasListEvent event) {
        onResume();
    }
}

