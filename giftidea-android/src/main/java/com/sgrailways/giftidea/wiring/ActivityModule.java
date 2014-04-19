package com.sgrailways.giftidea.wiring;

import com.sgrailways.giftidea.AboutActivity;
import com.sgrailways.giftidea.actions.StdActionFactory;
import com.sgrailways.statham.ActionFactory;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(injects = {
    AboutActivity.class
})
public class ActivityModule {

    @Provides @Singleton ActionFactory provideActionFactory() {
        return new StdActionFactory();
    }
}
