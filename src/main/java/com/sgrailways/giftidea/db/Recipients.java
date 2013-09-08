package com.sgrailways.giftidea.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.inject.Inject;
import com.sgrailways.giftidea.domain.MissingRecipient;
import com.sgrailways.giftidea.domain.Recipient;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.*;

public class Recipients {
    private final static String[] COLUMNS = new String[]{_ID, NAME, IDEAS_COUNT};
    private SQLiteDatabase writeableDatabase;

    @Inject
    public Recipients(Database database) {
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
        String now = now();
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

    private static String now() {
        return DateTime.now().toString(ISODateTimeFormat.basicDateTime());
    }

    private Recipient changeIdeaCountFor(Recipient recipient, long newIdeaCount) {
        ContentValues values = new ContentValues();
        values.put(Database.RecipientsTable.UPDATED_AT, now());
        values.put(Database.RecipientsTable.IDEAS_COUNT, newIdeaCount);
        writeableDatabase.update(TABLE_NAME, values, _ID + "=?", new String[]{String.valueOf(recipient.getId())});
        return new Recipient(recipient.getId(), recipient.getName(), newIdeaCount);
    }
}
