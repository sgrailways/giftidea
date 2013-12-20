package com.sgrailways.giftidea.actions;

import android.app.Activity;
import android.support.v4.app.NavUtils;
import com.sgrailways.statham.Action;

public class NavigateUpAction implements Action {
    private final Activity activity;

    public NavigateUpAction(Activity activity) {
        this.activity = activity;
    }

    @Override public boolean invoke() {
        NavUtils.navigateUpFromSameTask(activity);
        return true;
    }
}
