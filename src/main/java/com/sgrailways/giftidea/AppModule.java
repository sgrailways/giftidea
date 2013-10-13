package com.sgrailways.giftidea;

import com.google.inject.AbstractModule;
import com.squareup.otto.Bus;

public class AppModule extends AbstractModule {
    @Override protected void configure() {
        bind(Bus.class).toInstance(new Bus());
    }
}
