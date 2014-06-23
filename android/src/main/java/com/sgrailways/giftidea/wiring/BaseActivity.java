package com.sgrailways.giftidea.wiring;

import android.app.Activity;
import android.os.Bundle;
import com.sgrailways.giftidea.GiftIdeaApplication;
import dagger.ObjectGraph;

public class BaseActivity extends Activity {
    private ObjectGraph graph;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGraph().inject(this);
    }

    @Override protected void onDestroy() {
        graph = null;
        super.onDestroy();
    }

    public void inject(Object o) {
        getGraph().inject(o);
    }

    private ObjectGraph getGraph() {
        if (graph == null) {
            GiftIdeaApplication application = (GiftIdeaApplication) getApplication();
            ObjectGraph applicationGraph = application.getApplicationGraph();
            graph = applicationGraph.plus(new ActivityModule(this));
        }
        return graph;
    }
}
