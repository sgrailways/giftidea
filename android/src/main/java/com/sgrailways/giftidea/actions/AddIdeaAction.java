package com.sgrailways.giftidea.actions;

import android.app.Activity;
import android.content.Intent;
import com.sgrailways.giftidea.IdeaActivity;
import com.sgrailways.statham.Action;

public class AddIdeaAction implements Action {
    private final Activity activity;

    public AddIdeaAction(Activity activity) {
        this.activity = activity;
    }

    @Override public boolean invoke() {
        activity.startActivity(new Intent(activity, IdeaActivity.class));
        return true;
    }
}
