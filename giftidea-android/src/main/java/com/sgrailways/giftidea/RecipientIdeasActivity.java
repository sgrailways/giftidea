package com.sgrailways.giftidea;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.sgrailways.giftidea.db.Ideas;
import com.sgrailways.giftidea.events.DeleteIdeaEvent;
import com.sgrailways.giftidea.events.GotItEvent;
import com.sgrailways.giftidea.events.RefreshIdeasListEvent;
import com.sgrailways.giftidea.wiring.BaseActivity;
import com.sgrailways.statham.ActionFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class RecipientIdeasActivity extends BaseActivity {
    @Inject ActionFactory actionFactory;
    @Inject Bus bus;
    @Inject Ideas ideas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.recipients, new RecipientIdeasList()).commit();
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
                return actionFactory.create(item, this).invoke();
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override protected void onResume() {
        getFragmentManager().beginTransaction().replace(R.id.recipients, new RecipientIdeasList()).commit();
        super.onResume();
    }

    @Subscribe public void answerRefreshIdeasList(RefreshIdeasListEvent event) {
        onResume();
    }

    @Subscribe public void answerDeleteIdea(DeleteIdeaEvent event) {
        ideas.delete(event.getId());
    }

    @Subscribe public void answerGotIt(GotItEvent event) {
        ideas.gotIt(event.getId(), event.getRecipientName());
    }
}

