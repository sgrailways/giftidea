package com.sgrailways.giftidea.core.domain;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Locale;

public class Holiday {
    private final Locale locale;
    private final String name;
    private final DateTime celebratedAt;
    private final boolean isCelebrated;
    private final DateTime updatedAt;
    private final DateTime createdAt;


    public Holiday(Locale locale, String name, DateTime celebratedAt, boolean isCelebrated, DateTime updatedAt, DateTime createdAt) {
        this.locale = locale;
        this.name = name;
        this.celebratedAt = celebratedAt;
        this.isCelebrated = isCelebrated;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public boolean isCelebrated() {
        return isCelebrated;
    }

    public String getLocale() {
        return locale.getLanguage() + "-" + locale.getCountry() + "-" + locale.getVariant();
    }

    public String getCelebratedAt() {
        return celebratedAt.toString(ISODateTimeFormat.basicDateTime());
    }

    public String getCreatedAt() {
        return createdAt.toString(ISODateTimeFormat.basicDateTime());
    }

    public String getUpdatedAt() {
        return updatedAt.toString(ISODateTimeFormat.basicDateTime());
    }
}
