package com.sgrailways.giftidea.core.domain;

public class NullIdea extends Idea {
    public NullIdea() {
        super(Long.MIN_VALUE, "", false, Long.MIN_VALUE);
    }
}
