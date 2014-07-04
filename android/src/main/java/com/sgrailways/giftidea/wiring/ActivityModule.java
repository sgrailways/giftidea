package com.sgrailways.giftidea.wiring;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.sgrailways.giftidea.ListenerFactory;
import com.sgrailways.giftidea.RecipientIdeasList;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(
        library = true,
        addsTo = AndroidModule.class,
        injects = {
                ListenerFactory.class,
                RecipientIdeasList.class
        }
)
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides @Singleton @ForActivity Context providesActivityContext() {
        return activity;
    }

    @Provides @Singleton SharedPreferences providesSharedPreferences() {
        return activity.getSharedPreferences("giftidea.pref", Context.MODE_PRIVATE);
    }
}
