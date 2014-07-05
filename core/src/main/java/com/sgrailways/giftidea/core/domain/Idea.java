package com.sgrailways.giftidea.core.domain;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class Idea {
    private final long id;
    private final String text;
    private final boolean done;
    private final long recipientId;
    private final String photoUri;

    public Idea(long id, String text, boolean isDone, long recipientId, String photoUri) {
        this.id = id;
        this.text = text;
        this.done = isDone;
        this.recipientId = recipientId;
        this.photoUri = photoUri;
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

    public File getPhoto() {
        return (photoUri == null || "".equals(photoUri)) ? null : new File(URI.create(photoUri).getPath());
    }

    @Override public String toString() {
        return "Idea{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", done=" + done +
                ", recipientId=" + recipientId +
                ", photoUri='" + photoUri + '\'' +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Idea idea = (Idea) o;
        return Objects.equals(done, idea.done) &&
                Objects.equals(id, idea.id) &&
                Objects.equals(recipientId, idea.recipientId) &&
                Objects.equals(text, idea.text) &&
                Objects.equals(photoUri, idea.photoUri);
    }

    @Override public int hashCode() {
        return Objects.hash(done, id, recipientId, text, photoUri);
    }

    public boolean hasPhoto() {
        return getPhoto() != null;
    }

    public String getPhotoUri() {
        return photoUri;
    }
}
