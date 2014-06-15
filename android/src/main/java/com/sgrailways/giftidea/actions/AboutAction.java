package com.sgrailways.giftidea.actions;

import android.app.Activity;
import android.content.Intent;
import com.sgrailways.giftidea.AboutActivity;
import com.sgrailways.statham.Action;

public class AboutAction implements Action {
    private final Activity activity;

    public AboutAction(Activity activity) {
        this.activity = activity;
    }

    @Override public boolean invoke() {
        activity.startActivity(new Intent(activity, AboutActivity.class));
        return true;
    }
}
