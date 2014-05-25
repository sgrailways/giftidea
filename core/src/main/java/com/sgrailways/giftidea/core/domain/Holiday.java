package com.sgrailways.giftidea.core.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Locale;

public class Holiday {
    private final long id;
    private final Locale locale;
    private final String name;
    private final LocalDate celebratedAt;
    private final boolean isCelebrated;
    private final DateTime updatedAt;
    private final DateTime createdAt;

    public Holiday(long id, Locale locale, String name, LocalDate celebratedAt, boolean isCelebrated, DateTime updatedAt, DateTime createdAt) {
        this.id = id;
        this.locale = locale;
        this.name = name;
        this.celebratedAt = celebratedAt;
        this.isCelebrated = isCelebrated;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public Holiday(Locale locale, String name, LocalDate celebratedAt, boolean isCelebrated, DateTime updatedAt, DateTime createdAt) {
        this(-1L, locale, name, celebratedAt, isCelebrated, updatedAt, createdAt);
    }

    public long getId() {
        return id;
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
        return celebratedAt.toString(ISODateTimeFormat.basicDate());
    }

    public String getCreatedAt() {
        return createdAt.toString(ISODateTimeFormat.basicDateTime());
    }

    public String getUpdatedAt() {
        return updatedAt.toString(ISODateTimeFormat.basicDateTime());
    }

    public LocalDate getCelebratedAtLocalDate() {
        return celebratedAt;
    }
}
