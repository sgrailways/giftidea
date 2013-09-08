package com.sgrailways.giftidea.domain;

public class MissingRecipient extends Recipient {

    public MissingRecipient() {
        super(Long.MIN_VALUE, "", Long.MIN_VALUE);
    }
}
