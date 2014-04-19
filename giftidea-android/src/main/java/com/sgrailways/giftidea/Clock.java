package com.sgrailways.giftidea;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class Clock {
    public String now() {
        return DateTime.now().toString(ISODateTimeFormat.basicDateTime());
    }
}
