package com.sgrailways.giftidea;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Session {
    private String recipientName;

    @Inject
    public Session() {
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}
