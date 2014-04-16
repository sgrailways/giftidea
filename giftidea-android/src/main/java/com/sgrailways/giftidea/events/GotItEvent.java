package com.sgrailways.giftidea.events;

public class GotItEvent {
    private final long id;
    private final String recipientName;

    public GotItEvent(long id, String recipientName) {
        this.id = id;
        this.recipientName = recipientName;
    }

    public long getId() {
        return id;
    }

    public String getRecipientName() {
        return recipientName;
    }
}
