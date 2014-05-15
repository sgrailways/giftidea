package com.sgrailways.giftidea.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.sgrailways.giftidea.Clock;
import com.sgrailways.giftidea.core.domain.MissingRecipient;
import com.sgrailways.giftidea.core.domain.Recipient;

import javax.inject.Inject;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.IDEAS_COUNT;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.NAME;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.TABLE_NAME;

public class Recipients {
    private final static String[] COLUMNS = new String[]{_ID, NAME, IDEAS_COUNT};
    private final Clock clock;
    private final SQLiteDatabase writeableDatabase;

    @Inject
    public Recipients(Database database, Clock clock) {
        this.clock = clock;
        this.writeableDatabase = database.getWritableDatabase();
    }

    public Recipient findById(long id) {
        Cursor cursor = writeableDatabase.query(TABLE_NAME, COLUMNS, _ID + "=?", new String[]{String.valueOf(id)}, null, null, null, "1");
        if(!cursor.moveToFirst()) {
            return new MissingRecipient();
        }
        Recipient recipient = new Recipient(cursor.getLong(0), cursor.getString(1), cursor.getLong(2));
        cursor.close();
        return recipient;
    }

    public Recipient findByName(String name) {
        Cursor cursor = writeableDatabase.query(TABLE_NAME, COLUMNS, NAME + "=?", new String[]{name}, null, null, null, "1");
        if(!cursor.moveToFirst()) {
            return new MissingRecipient();
        }
        Recipient recipient = new Recipient(cursor.getLong(0), cursor.getString(1), cursor.getLong(2));
        cursor.close();
        return recipient;
    }

    public Cursor findAllOrderedByName() {
        return writeableDatabase.query(TABLE_NAME, COLUMNS, null, null, null, null, NAME + " ASC");
    }

    public Recipient createFromName(String name) {
        String now = clock.now();
        ContentValues values = new ContentValues();
        values.put(Database.RecipientsTable.NAME, name);
        values.put(Database.RecipientsTable.IDEAS_COUNT, 1L);
        values.put(Database.RecipientsTable.CREATED_AT, now);
        values.put(Database.RecipientsTable.UPDATED_AT, now);
        long id = writeableDatabase.insert(Database.RecipientsTable.TABLE_NAME, null, values);
        return new Recipient(id, name, 1L);
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
        writeableDatabase.update(TABLE_NAME, values, _ID + "=?", new String[]{String.valueOf(recipient.getId())});
        return new Recipient(recipient.getId(), recipient.getName(), newIdeaCount);
    }

    public Recipient decrementIdeaCountFor(long recipientId) {
        Recipient recipient = findById(recipientId);
        return decrementIdeaCountFor(recipient);
    }
}
