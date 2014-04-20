package com.sgrailways.giftidea.core.domain;

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
        return "Idea{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", done=" + done +
                ", recipientId=" + recipientId +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Idea idea = (Idea) o;

        if (done != idea.done) return false;
        if (id != idea.id) return false;
        if (recipientId != idea.recipientId) return false;
        if (text != null ? !text.equals(idea.text) : idea.text != null) return false;

        return true;
    }

    @Override public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (done ? 1 : 0);
        result = 31 * result + (int) (recipientId ^ (recipientId >>> 32));
        return result;
    }
}
