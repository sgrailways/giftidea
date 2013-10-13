package com.sgrailways.giftidea;

import android.content.Context;
import android.widget.Toast;
import com.google.inject.Inject;
import roboguice.inject.ContextSingleton;

@ContextSingleton
public class Toaster {
    private final Context context;

    @Inject
    public Toaster(Context context) {
        this.context = context;
    }

    public void show(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
