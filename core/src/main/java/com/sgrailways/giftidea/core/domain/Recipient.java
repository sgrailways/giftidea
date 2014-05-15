package com.sgrailways.giftidea.core.domain;

public class Recipient {
    private final long id;
    private final String name;
    private final long ideaCount;

    public Recipient(long id, String name, long ideaCount) {
        this.id = id;
        this.name = name;
        this.ideaCount = ideaCount;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getIdeaCount() {
        return ideaCount;
    }
}
