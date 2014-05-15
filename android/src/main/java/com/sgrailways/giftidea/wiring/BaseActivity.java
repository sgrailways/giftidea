package com.sgrailways.giftidea.wiring;

import android.app.Activity;
import android.os.Bundle;
import com.sgrailways.giftidea.GiftIdeaApplication;
import dagger.ObjectGraph;

public class BaseActivity extends Activity {
    private ObjectGraph graph;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        graph = ((GiftIdeaApplication) getApplication()).getApplicationGraph().plus(new ActivityModule(this));
        graph.inject(this);
    }

    @Override protected void onDestroy() {
        graph = null;
        super.onDestroy();
    }

    public void inject(Object o) {
        graph.inject(o);
    }
}
