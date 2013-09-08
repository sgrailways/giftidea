package com.sgrailways.giftidea;

import com.google.inject.Singleton;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

@Singleton
public class GiftIdeaFormat {
    public String now() {
        return DateTime.now().toString(ISODateTimeFormat.basicDateTime());
    }
}
