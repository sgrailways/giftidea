package com.sgrailways.giftidea.actions;

import android.app.Activity;
import android.view.MenuItem;
import com.sgrailways.giftidea.R;
import com.sgrailways.statham.Action;
import com.sgrailways.statham.ActionFactory;

public class Statham implements ActionFactory {
    @Override public Action create(MenuItem item, Activity activity) {
        switch (item.getItemId()) {
            case R.id.action_add:
                return new AddIdeaAction(activity);
            case R.id.action_about:
                return new AboutAction(activity);
            case android.R.id.home:
                return new NavigateUpAction(activity);
            default:
                throw new RuntimeException("Unhandled MenuItem.");
        }
    }
}
