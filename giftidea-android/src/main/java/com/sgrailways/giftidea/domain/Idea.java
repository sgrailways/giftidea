package com.sgrailways.giftidea.domain;

import com.google.common.base.Objects;

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
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("text", text)
                .add("done", done)
                .toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Idea that = (Idea) o;
        return Objects.equal(this, that);
    }

    @Override public int hashCode() {
        return Objects.hashCode(this);
    }
}
