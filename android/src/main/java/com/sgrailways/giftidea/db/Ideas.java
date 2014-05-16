package com.sgrailways.giftidea.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.google.common.base.CharMatcher;
import com.sgrailways.giftidea.Clock;
import com.sgrailways.giftidea.core.HashTagLocator;
import com.sgrailways.giftidea.core.domain.Idea;
import com.sgrailways.giftidea.core.domain.MissingIdea;
import com.sgrailways.giftidea.core.domain.MissingRecipient;
import com.sgrailways.giftidea.core.domain.Recipient;

import javax.inject.Inject;
import java.util.LinkedHashSet;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.IdeasTable.RECIPIENT_ID;

public class Ideas {
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.sgrailways.giftidea_ideas";
    public static final String CONTENT_IDEA_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.sgrailways.giftidea_ideas";
    private final Recipients recipients;
    private final Clock clock;
    private final ContentResolver resolver;
    private final HashTagLocator hashTagLocator;
    public final static String[] COLUMNS = new String[]{
            Database.IdeasTable._ID,
            Database.IdeasTable.IDEA,
            Database.IdeasTable.IS_DONE,
            Database.IdeasTable.RECIPIENT_ID
    };
    public static final Uri URI = Uri.parse("content://com.sgrailways.giftidea/ideas");

    @Inject
    public Ideas(Recipients recipients, Clock clock, HashTagLocator hashTagLocator, ContentResolver resolver) {
        this.hashTagLocator = hashTagLocator;
        this.recipients = recipients;
        this.clock = clock;
        this.resolver = resolver;
    }

    public Idea findById(long id) {
        Cursor cursor = resolver.query(URI, COLUMNS, _ID + "=?", new String[]{String.valueOf(id)}, null);
        if(!cursor.moveToFirst()) {
            return new MissingIdea();
        }
        return new Idea(cursor.getLong(0), cursor.getString(1), Boolean.parseBoolean(cursor.getString(2)), cursor.getLong(3));
    }

    public void delete(long id) {
        Idea idea = findById(id);
        if (!idea.isDone()) {
            recipients.decrementIdeaCountFor(idea.getRecipientId());
        }
        resolver.delete(URI, _ID + "=?", new String[]{String.valueOf(id)});
        Cursor cursor = resolver.query(URI, new String[]{_ID}, RECIPIENT_ID + "=?", new String[]{String.valueOf(idea.getRecipientId())}, null);
        if (cursor.getCount() == 0) {
            resolver.delete(Recipients.URI, _ID + "=?", new String[]{String.valueOf(idea.getRecipientId())});
        }
    }

    public void update(long id, String idea) {
        ContentValues ideaValues = new ContentValues();
        ideaValues.put(Database.IdeasTable.IDEA, CharMatcher.WHITESPACE.collapseFrom(idea, ' '));
        ideaValues.put(Database.IdeasTable.UPDATED_AT, clock.now());
        resolver.update(URI, ideaValues, _ID + "=?", new String[]{String.valueOf(id)});
    }

    public void createFromText(String idea) {
        String now = clock.now();
        ContentValues ideaValues = new ContentValues();
        ideaValues.put(Database.IdeasTable.IDEA, hashTagLocator.removeAllFrom(idea));
        ideaValues.put(Database.IdeasTable.IS_DONE, String.valueOf(false));
        ideaValues.put(Database.IdeasTable.CREATED_AT, now);
        ideaValues.put(Database.IdeasTable.UPDATED_AT, now);
        LinkedHashSet<String> hashTags = hashTagLocator.findAllIn(idea);
        for (String hashTag : hashTags) {
            Recipient recipient = recipients.findByName(hashTag);
            long recipientId;
            if (recipient instanceof MissingRecipient) {
                recipientId = recipients.createFromName(hashTag).getId();
            } else {
                recipientId = recipient.getId();
                recipients.incrementIdeaCountFor(recipient);
            }
            ideaValues.put(Database.IdeasTable.RECIPIENT_ID, recipientId);
            resolver.insert(URI, ideaValues);
        }
    }

    public void gotIt(long id, String recipientName) {
        ContentValues values = new ContentValues();
        values.put(Database.IdeasTable.IS_DONE, String.valueOf(true));
        values.put(Database.IdeasTable.UPDATED_AT, clock.now());
        resolver.update(URI, values, _ID + "=?", new String[]{String.valueOf(id)});

        Recipient recipient = recipients.findByName(recipientName);
        if(!(recipient instanceof MissingRecipient)) {
            recipients.decrementIdeaCountFor(recipient);
        }
    }
}
