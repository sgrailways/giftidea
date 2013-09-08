package com.sgrailways.giftidea.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import com.google.inject.Inject;
import com.sgrailways.giftidea.Clock;
import com.sgrailways.giftidea.HashTagLocator;
import com.sgrailways.giftidea.domain.Idea;
import com.sgrailways.giftidea.domain.MissingIdea;
import com.sgrailways.giftidea.domain.MissingRecipient;
import com.sgrailways.giftidea.domain.Recipient;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;

import static com.sgrailways.giftidea.db.Database.IdeasTable.IS_DONE;
import static com.sgrailways.giftidea.db.Database.IdeasTable.TABLE_NAME;

public class Ideas {
    private final SQLiteDatabase writeableDatabase;
    private final Recipients recipients;
    private final Clock clock;
    private final HashTagLocator hashTagLocator;
    private final static String[] COLUMNS = new String[]{Database.IdeasTable._ID, Database.IdeasTable.IDEA, Database.IdeasTable.IS_DONE};

    @Inject
    public Ideas(Database database, Recipients recipients, Clock clock, HashTagLocator hashTagLocator) {
        this.hashTagLocator = hashTagLocator;
        this.writeableDatabase = database.getWritableDatabase();
        this.recipients = recipients;
        this.clock = clock;
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

    public Cursor findAllForRecipientName(String name) {
        String recipientId = String.valueOf(recipients.findByName(name).getId());
        return writeableDatabase.query(TABLE_NAME, COLUMNS, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{recipientId}, null, null, IS_DONE + " ASC");
    }

    public Remaining delete(long id) {
        Remaining ideasRemaining;
        try {
            writeableDatabase.beginTransaction();
            Cursor cursor = writeableDatabase.query(TABLE_NAME, new String[]{Database.IdeasTable.RECIPIENT_ID}, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
            cursor.moveToFirst();
            long recipientId = cursor.getLong(0);
            cursor.close();
            writeableDatabase.delete(TABLE_NAME, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)});
            //TODO: remove this for api-10 compatibility
            long ideasForRecipient = DatabaseUtils.queryNumEntries(writeableDatabase, TABLE_NAME, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{String.valueOf(recipientId)});
            long activeIdeasForRecipient = DatabaseUtils.queryNumEntries(writeableDatabase, TABLE_NAME, Database.IdeasTable.RECIPIENT_ID + "=? AND " + Database.IdeasTable.IS_DONE + "=?", new String[]{String.valueOf(recipientId), String.valueOf(false)});
            if (ideasForRecipient == 0) {
                writeableDatabase.delete(Database.RecipientsTable.TABLE_NAME, Database.RecipientsTable._ID + "=?", new String[]{String.valueOf(recipientId)});
                ideasRemaining = Remaining.NO;
            } else if(activeIdeasForRecipient > 0) {
                recipients.decrementIdeaCountFor(recipients.findById(recipientId));
                ideasRemaining = Remaining.YES;
            } else {
                ideasRemaining = Remaining.YES;
            }
            writeableDatabase.setTransactionSuccessful();
            return ideasRemaining;
        } finally {
            writeableDatabase.endTransaction();
        }
    }

    public Remaining forRecipient(String recipientName) {
        long recipientId = recipients.findByName(recipientName).getId();
        //TODO: remove this for api-10 compatibility
        long ideasCount = DatabaseUtils.queryNumEntries(writeableDatabase, TABLE_NAME, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{String.valueOf(recipientId)});
        return ideasCount == 0 ? Remaining.NO : Remaining.YES;
    }

    public void update(long id, String idea) {
        ContentValues ideaValues = new ContentValues();
        ideaValues.put(Database.IdeasTable.IDEA, StringUtils.normalizeSpace(idea));
        ideaValues.put(Database.IdeasTable.UPDATED_AT, clock.now());
        try {
            writeableDatabase.beginTransaction();
            writeableDatabase.update(TABLE_NAME, ideaValues, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)});
            writeableDatabase.setTransactionSuccessful();
        } finally {
            writeableDatabase.endTransaction();
        }
    }

    public void createFromText(String idea) {
        String now = clock.now();
        ContentValues ideaValues = new ContentValues();
        ideaValues.put(Database.IdeasTable.IDEA, hashTagLocator.removeAllFrom(idea));
        ideaValues.put(Database.IdeasTable.IS_DONE, String.valueOf(false));
        ideaValues.put(Database.IdeasTable.CREATED_AT, now);
        ideaValues.put(Database.IdeasTable.UPDATED_AT, now);
        LinkedHashSet<String> hashTags = hashTagLocator.findAllIn(idea);
        try {
            writeableDatabase.beginTransaction();
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
                writeableDatabase.insert(Database.IdeasTable.TABLE_NAME, null, ideaValues);
            }
            writeableDatabase.setTransactionSuccessful();
        } finally {
            writeableDatabase.endTransaction();
        }
    }

    public void gotIt(long id, String recipientName) {
        ContentValues values = new ContentValues();
        values.put(Database.IdeasTable.IS_DONE, String.valueOf(true));
        values.put(Database.IdeasTable.UPDATED_AT, clock.now());
        writeableDatabase.update(Database.IdeasTable.TABLE_NAME, values, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)});

        Recipient recipient = recipients.findByName(recipientName);
        if(!(recipient instanceof MissingRecipient)) {
            recipients.decrementIdeaCountFor(recipient);
        }
    }

    public enum Remaining {
        YES, NO
    }
}
