package com.sgrailways.giftidea;

import android.content.Context;
import android.widget.Toast;
import com.sgrailways.giftidea.wiring.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Toaster {
    private final Context context;

    @Inject
    public Toaster(@ForApplication Context context) {
        this.context = context;
    }

    public void show(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
