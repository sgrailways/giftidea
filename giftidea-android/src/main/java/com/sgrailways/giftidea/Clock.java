package com.sgrailways.giftidea;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Clock {

    @Inject
    public Clock() {
    }

    public String now() {
        return DateTime.now().toString(ISODateTimeFormat.basicDateTime());
    }
}
