package com.sgrailways.giftidea.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.sgrailways.giftidea.Clock;
import com.sgrailways.giftidea.core.domain.MissingRecipient;
import com.sgrailways.giftidea.core.domain.Recipient;

import javax.inject.Inject;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.IDEAS_COUNT;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.NAME;

public class Recipients {
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.sgrailways.giftidea_recipients";
    public static final String CONTENT_RECIPIENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.sgrailways.giftidea_recipients";
    private final static String[] COLUMNS = new String[]{_ID, NAME, IDEAS_COUNT};
    public static final Uri URI = Uri.parse("content://com.sgrailways.giftidea/recipients");
    private final Clock clock;
    private final ContentResolver resolver;

    @Inject
    public Recipients(Clock clock, ContentResolver resolver) {
        this.clock = clock;
        this.resolver = resolver;
    }

    public Recipient findById(long id) {
        Cursor cursor = resolver.query(URI, COLUMNS, _ID + "=?", new String[]{String.valueOf(id)}, null);
        if(!cursor.moveToFirst()) {
            return new MissingRecipient();
        }
        return new Recipient(cursor.getLong(0), cursor.getString(1), cursor.getLong(2));
    }

    public Recipient findByName(String name) {
        Cursor cursor = resolver.query(URI, COLUMNS, NAME + "=?", new String[]{name}, null);
        if(!cursor.moveToFirst()) {
            return new MissingRecipient();
        }
        return new Recipient(cursor.getLong(0), cursor.getString(1), cursor.getLong(2));
    }

    public Recipient createFromName(String name) {
        String now = clock.now();
        ContentValues values = new ContentValues();
        values.put(Database.RecipientsTable.NAME, name);
        values.put(Database.RecipientsTable.IDEAS_COUNT, 1L);
        values.put(Database.RecipientsTable.CREATED_AT, now);
        values.put(Database.RecipientsTable.UPDATED_AT, now);
        Uri uri = resolver.insert(URI, values);
        return new Recipient(Long.valueOf(uri.getLastPathSegment()), name, 1L);
    }

    public Recipient incrementIdeaCountFor(Recipient recipient) {
        return changeIdeaCountFor(recipient, recipient.getIdeaCount() + 1L);
    }

    public Recipient decrementIdeaCountFor(Recipient recipient) {
        return changeIdeaCountFor(recipient, recipient.getIdeaCount() - 1L);
    }

    private Recipient changeIdeaCountFor(Recipient recipient, long newIdeaCount) {
        ContentValues values = new ContentValues();
        values.put(Database.RecipientsTable.UPDATED_AT, clock.now());
        values.put(Database.RecipientsTable.IDEAS_COUNT, newIdeaCount);
        resolver.update(URI, values, _ID + "=?", new String[]{String.valueOf(recipient.getId())});
        return new Recipient(recipient.getId(), recipient.getName(), newIdeaCount);
    }

    public Recipient decrementIdeaCountFor(long recipientId) {
        Recipient recipient = findById(recipientId);
        return decrementIdeaCountFor(recipient);
    }
}
