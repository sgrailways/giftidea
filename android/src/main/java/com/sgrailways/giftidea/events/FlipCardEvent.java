package com.sgrailways.giftidea.events;

public class FlipCardEvent {
    public final boolean toFront;

    public FlipCardEvent(boolean toFront) {
        this.toFront = toFront;
    }
}
