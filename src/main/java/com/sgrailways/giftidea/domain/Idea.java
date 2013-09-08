package com.sgrailways.giftidea.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Idea {
    private final long id;
    private final String text;

    public Idea(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }
}
