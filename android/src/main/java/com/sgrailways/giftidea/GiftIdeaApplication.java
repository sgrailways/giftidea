package com.sgrailways.giftidea;

import android.app.Application;
import com.sgrailways.giftidea.db.Database;
import com.sgrailways.giftidea.wiring.AndroidModule;
import dagger.ObjectGraph;
import timber.log.Timber;

import javax.inject.Inject;

public class GiftIdeaApplication extends Application {
    private ObjectGraph graph;
    @Inject Database database;

    @Override public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        graph = ObjectGraph.create(new AndroidModule(this));
        graph.inject(this);
        database.upgrade();
    }

    public void inject(Object o) {
        graph.inject(o);
    }

    public ObjectGraph getApplicationGraph() {
        return graph;
    }
}
