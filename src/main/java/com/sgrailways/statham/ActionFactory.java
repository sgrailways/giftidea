package com.sgrailways.statham;

import android.app.Activity;
import android.view.MenuItem;

public interface ActionFactory {
    Action create(MenuItem item, Activity activity);
}
