package com.sgrailways.giftidea.domain;

public class MissingIdea extends Idea {
    public MissingIdea() {
        super(Long.MIN_VALUE, "", false, Long.MIN_VALUE);
    }
}
