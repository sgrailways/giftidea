package com.sgrailways;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.sgrailways.giftidea.NewIdeaActivity;
import com.sgrailways.giftidea.RecipientsList;
import roboguice.activity.RoboFragmentActivity;

public class HelloAndroidActivity extends RoboFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.recipients, new RecipientsList()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.sgrailways.R.menu.main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(new Intent(this, NewIdeaActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

