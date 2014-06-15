package com.sgrailways.giftidea.core.domain;

public class NullRecipient extends Recipient {

    public NullRecipient() {
        super(Long.MIN_VALUE, "", Long.MIN_VALUE);
    }
}
