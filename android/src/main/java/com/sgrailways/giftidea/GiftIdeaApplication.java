package com.sgrailways.giftidea;

import android.app.Application;
import android.content.SharedPreferences;
import com.sgrailways.giftidea.db.Database;
import com.sgrailways.giftidea.wiring.AndroidModule;
import dagger.ObjectGraph;
import timber.log.Timber;

import javax.inject.Inject;

public class GiftIdeaApplication extends Application {
    private ObjectGraph graph;
    @Inject Database database;
    @Inject SharedPreferences preferences;

    @Override public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        graph = getApplicationGraph();
        graph.inject(this);
        database.upgrade();
        IdeaImageUtility.destroyPendingIdeaImage(preferences); // if the app is exited abruptly, it could leave behind unwanted files
    }

    public void inject(Object o) {
        getApplicationGraph().inject(o);
    }

    public ObjectGraph getApplicationGraph() {
        if (graph == null) {
            graph = ObjectGraph.create(new AndroidModule(this));
        }
        return graph;
    }
}
