package com.sgrailways.giftidea.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Idea {
    private final long id;
    private final String text;
    private final boolean done;
    private final long recipientId;

    public Idea(long id, String text, boolean isDone, long recipientId) {
        this.id = id;
        this.text = text;
        done = isDone;
        this.recipientId = recipientId;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isDone() {
        return done;
    }

    public long getRecipientId() {
        return recipientId;
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
