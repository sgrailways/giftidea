package com.sgrailways.giftidea.wiring;

import android.content.Context;
import com.sgrailways.giftidea.AboutActivity;
import com.sgrailways.giftidea.Clock;
import com.sgrailways.giftidea.EditIdeaFragment;
import com.sgrailways.giftidea.GiftIdeaApplication;
import com.sgrailways.giftidea.IdeaActivity;
import com.sgrailways.giftidea.MainActivity;
import com.sgrailways.giftidea.NewIdeaFragment;
import com.sgrailways.giftidea.RecipientIdeasActivity;
import com.sgrailways.giftidea.RecipientsList;
import com.sgrailways.giftidea.Session;
import com.sgrailways.giftidea.actions.StdActionFactory;
import com.sgrailways.giftidea.core.HashTagLocator;
import com.sgrailways.statham.ActionFactory;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(injects = {
        AboutActivity.class,
        IdeaActivity.class,
        MainActivity.class,
        RecipientsList.class,
        RecipientIdeasActivity.class,
        NewIdeaFragment.class,
        EditIdeaFragment.class
},
        library = true)
public class AndroidModule {
    private GiftIdeaApplication application;

    public AndroidModule(GiftIdeaApplication application) {
        this.application = application;
    }

    @Provides @Singleton @ForApplication Context providesApplicationContext() {
        return application;
    }

    @Provides @Singleton Bus providesBus() {
        return new Bus();
    }

    @Provides @Singleton Clock providesClock() {
        return new Clock();
    }

    @Provides @Singleton HashTagLocator providesHashTagLocator() {
        return new HashTagLocator();
    }

    @Provides @Singleton ActionFactory provideActionFactory() {
        return new StdActionFactory();
    }

    @Provides @Singleton Session providesSession() { return new Session(); }
}
