package com.sgrailways.giftidea.events;

public class DeleteIdeaEvent {
    private final long id;

    public DeleteIdeaEvent(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
