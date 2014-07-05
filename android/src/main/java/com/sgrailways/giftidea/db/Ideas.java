package com.sgrailways.giftidea.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import com.sgrailways.giftidea.Clock;
import com.sgrailways.giftidea.IdeaImageUtility;
import com.sgrailways.giftidea.core.HashTagLocator;
import com.sgrailways.giftidea.core.domain.Idea;
import com.sgrailways.giftidea.core.domain.NullIdea;
import com.sgrailways.giftidea.core.domain.NullRecipient;
import com.sgrailways.giftidea.core.domain.Recipient;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.IdeasTable.RECIPIENT_ID;

public class Ideas {
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.sgrailways.giftidea_ideas";
    public static final String CONTENT_IDEA_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.sgrailways.giftidea_ideas";
    public static final String DEFAULT_SORT = String.format("%s ASC, %s ASC", Database.IdeasTable.IS_DONE, _ID);
    public static final String QUERY_BY_RECIPIENT_ID = RECIPIENT_ID + "=?";
    public static final String QUERY_BY_ID = _ID + "=?";
    private final Recipients recipients;
    private final Clock clock;
    private final ContentResolver resolver;
    private final HashTagLocator hashTagLocator;
    public final static String[] COLUMNS = new String[]{
            Database.IdeasTable._ID,
            Database.IdeasTable.IDEA,
            Database.IdeasTable.IS_DONE,
            Database.IdeasTable.RECIPIENT_ID,
            Database.IdeasTable.IMAGE_URI
    };
    public final static Map<String, Integer> COLUMN_INDEXES = ImmutableMap.of(
            Database.IdeasTable._ID, 0,
            Database.IdeasTable.IDEA, 1,
            Database.IdeasTable.IS_DONE, 2,
            Database.IdeasTable.RECIPIENT_ID, 3,
            Database.IdeasTable.IMAGE_URI, 4
    );
    public static final Uri URI = Uri.parse("content://com.sgrailways.giftidea/ideas");

    @Inject
    public Ideas(Recipients recipients, Clock clock, HashTagLocator hashTagLocator, ContentResolver resolver) {
        this.hashTagLocator = hashTagLocator;
        this.recipients = recipients;
        this.clock = clock;
        this.resolver = resolver;
    }

    public Idea findById(long id) {
        Cursor cursor = resolver.query(URI, COLUMNS, QUERY_BY_ID, new String[]{String.valueOf(id)}, null);
        if (!cursor.moveToFirst()) {
            return new NullIdea();
        }
        return new Idea(cursor.getLong(0), cursor.getString(1), Boolean.parseBoolean(cursor.getString(2)), cursor.getLong(3), cursor.getString(4));
    }

    public void delete(long id) {
        Idea idea = findById(id);
        if (!idea.isDone()) {
            recipients.decrementIdeaCountFor(idea.getRecipientId());
        }
        IdeaImageUtility.destroyImage(idea.getPhotoUri());
        resolver.delete(URI, QUERY_BY_ID, new String[]{String.valueOf(id)});
        Cursor cursor = resolver.query(URI, new String[]{_ID}, RECIPIENT_ID + "=?", new String[]{String.valueOf(idea.getRecipientId())}, null);
        if (cursor.getCount() == 0) {
            resolver.delete(Recipients.URI, QUERY_BY_ID, new String[]{String.valueOf(idea.getRecipientId())});
        }
    }

    public void update(long id, String idea) {
        ContentValues ideaValues = new ContentValues();
        ideaValues.put(Database.IdeasTable.IDEA, CharMatcher.WHITESPACE.collapseFrom(idea, ' '));
        ideaValues.put(Database.IdeasTable.UPDATED_AT, clock.now());
        resolver.update(URI, ideaValues, QUERY_BY_ID, new String[]{String.valueOf(id)});
    }

    public void updateImageUrl(long id, String url) {
        File photo = findById(id).getPhoto();
        if (photo != null && photo.exists()) {
            if (photo.delete()) {
                Timber.d("Existing photo deleted for image %d", id);
            } else {
                Timber.d("Existing photo for idea %d failed to delete", id);
            }
        }
        ContentValues ideaValues = new ContentValues();
        ideaValues.put(Database.IdeasTable.UPDATED_AT, clock.now());
        ideaValues.put(Database.IdeasTable.IMAGE_URI, url);
        resolver.update(URI, ideaValues, QUERY_BY_ID, new String[]{String.valueOf(id)});
        Timber.d("Updated idea #%d with image '%s'", id, url);
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
            if (recipient instanceof NullRecipient) {
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
        resolver.update(URI, values, QUERY_BY_ID, new String[]{String.valueOf(id)});

        Recipient recipient = recipients.findByName(recipientName);
        if (!(recipient instanceof NullRecipient)) {
            recipients.decrementIdeaCountFor(recipient);
        }
    }
}
