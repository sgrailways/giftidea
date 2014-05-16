package com.sgrailways.giftidea.wiring;

import android.content.ContentResolver;
import android.content.Context;
import com.sgrailways.giftidea.AboutActivity;
import com.sgrailways.giftidea.EditIdeaFragment;
import com.sgrailways.giftidea.GiftIdeaApplication;
import com.sgrailways.giftidea.IdeaActivity;
import com.sgrailways.giftidea.MainActivity;
import com.sgrailways.giftidea.NewIdeaFragment;
import com.sgrailways.giftidea.RecipientIdeasActivity;
import com.sgrailways.giftidea.RecipientsList;
import com.sgrailways.giftidea.actions.Statham;
import com.sgrailways.giftidea.db.Ideas;
import com.sgrailways.giftidea.db.Recipients;
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
        EditIdeaFragment.class,
        Recipients.class,
        Ideas.class
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

    @Provides @Singleton ActionFactory providesActionFactory() { return new Statham(); }

    @Provides ContentResolver providesContentResolver() { return application.getContentResolver(); }
}
