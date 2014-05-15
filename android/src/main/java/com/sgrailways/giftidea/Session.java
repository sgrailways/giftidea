package com.sgrailways.giftidea;

import com.sgrailways.giftidea.core.domain.Recipient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Session {
    private Recipient recipient;

    @Inject
    public Session() {
    }

    public void setActiveRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public String getActiveRecipientName() {
        return recipient.getName();
    }

    public String getActiveRecipientId() {
        return String.valueOf(recipient.getId());
    }

    public Recipient getActiveRecipient() {
        return recipient;
    }
}
