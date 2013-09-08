package com.sgrailways.giftidea.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import com.google.inject.Inject;
import com.sgrailways.giftidea.GiftIdeaFormat;
import com.sgrailways.giftidea.domain.Idea;
import com.sgrailways.giftidea.domain.MissingIdea;
import org.apache.commons.lang3.StringUtils;

import static com.sgrailways.giftidea.db.Database.IdeasTable.TABLE_NAME;

public class Ideas {
    private final SQLiteDatabase writeableDatabase;
    private final Database database;
    private final Recipients recipients;
    private final GiftIdeaFormat giftIdeaFormat;
    private final static String[] COLUMNS = new String[]{Database.IdeasTable._ID, Database.IdeasTable.IDEA};

    @Inject
    public Ideas(Database database, Recipients recipients, GiftIdeaFormat giftIdeaFormat) {
        this.writeableDatabase = database.getWritableDatabase();
        this.database = database;
        this.recipients = recipients;
        this.giftIdeaFormat = giftIdeaFormat;
    }

    public Idea findById(long id) {
        Cursor cursor = writeableDatabase.query(TABLE_NAME, COLUMNS, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)}, null, null, null, "1");
        if(!cursor.moveToFirst()) {
            return new MissingIdea();
        }
        Idea idea = new Idea(cursor.getLong(0), cursor.getString(1));
        cursor.close();
        return idea;
    }

    public Remaining delete(long id) {
        Remaining ideasRemaining;
        SQLiteDatabase wdb = database.getWritableDatabase();
        try {
            wdb.beginTransaction();
            Cursor cursor = wdb.query(TABLE_NAME, new String[]{Database.IdeasTable.RECIPIENT_ID}, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
            cursor.moveToFirst();
            long recipientId = cursor.getLong(0);
            cursor.close();
            wdb.delete(TABLE_NAME, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)});
            //TODO: remove this for api-10 compatibility
            long ideasForRecipient = DatabaseUtils.queryNumEntries(wdb, TABLE_NAME, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{String.valueOf(recipientId)});
            if (ideasForRecipient == 0) {
                wdb.delete(Database.RecipientsTable.TABLE_NAME, Database.RecipientsTable._ID + "=?", new String[]{String.valueOf(recipientId)});
                ideasRemaining = Remaining.NO;
            } else {
                recipients.decrementIdeaCountFor(recipients.findById(recipientId));
                ideasRemaining = Remaining.YES;
            }
            wdb.setTransactionSuccessful();
            return ideasRemaining;
        } finally {
            wdb.endTransaction();
        }
    }

    public Remaining forRecipient(String recipientName) {
        SQLiteDatabase wdb = database.getWritableDatabase();
        long recipientId = recipients.findByName(recipientName).getId();
        //TODO: remove this for api-10 compatibility
        long ideasCount = DatabaseUtils.queryNumEntries(wdb, TABLE_NAME, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{String.valueOf(recipientId)});
        return ideasCount == 0 ? Remaining.NO : Remaining.YES;
    }

    public void update(long id, String idea) {
        ContentValues ideaValues = new ContentValues();
        ideaValues.put(Database.IdeasTable.IDEA, StringUtils.normalizeSpace(idea));
        ideaValues.put(Database.IdeasTable.UPDATED_AT, giftIdeaFormat.now());
        try {
            writeableDatabase.beginTransaction();
            writeableDatabase.update(TABLE_NAME, ideaValues, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)});
            writeableDatabase.setTransactionSuccessful();
        } finally {
            writeableDatabase.endTransaction();
        }
    }

    public enum Remaining {
        YES, NO
    }
}
