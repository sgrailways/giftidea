package com.sgrailways.giftidea;

import android.app.Application;
import com.sgrailways.giftidea.wiring.AndroidModule;
import dagger.ObjectGraph;

public class GiftIdeaApplication extends Application {
    private ObjectGraph graph;

    @Override public void onCreate() {
        super.onCreate();
        graph = ObjectGraph.create(new AndroidModule(this));
    }

    public void inject(Object o) {
        graph.inject(o);
    }

    public ObjectGraph getApplicationGraph() {
        return graph;
    }
}
