package com.sgrailways.giftidea.events;

public class FlipCardEvent {
    public final long id;
    public final boolean toFront;

    public FlipCardEvent() {
        id = -1L;
        toFront = true;
    }

    public FlipCardEvent(long id) {
        this.id = id;
        this.toFront = false;
    }
}
