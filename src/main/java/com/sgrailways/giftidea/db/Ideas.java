package com.sgrailways.giftidea.db;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class Ideas {
    private final Database database;

    public Ideas(Database database) {
        this.database = database;
    }

    public Remaining delete(long id) {
        Remaining ideasRemaining;
        SQLiteDatabase wdb = database.getWritableDatabase();
        try {
            wdb.beginTransaction();
            Cursor cursor = wdb.query(Database.IdeasTable.TABLE_NAME, new String[]{Database.IdeasTable.RECIPIENT_ID}, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
            cursor.moveToFirst();
            long recipientId = cursor.getLong(0);
            cursor.close();
            wdb.delete(Database.IdeasTable.TABLE_NAME, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)});
            long ideasForRecipient = DatabaseUtils.queryNumEntries(wdb, Database.IdeasTable.TABLE_NAME, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{String.valueOf(recipientId)});
            if (ideasForRecipient == 0) {
                wdb.delete(Database.RecipientsTable.TABLE_NAME, Database.RecipientsTable._ID + "=?", new String[]{String.valueOf(recipientId)});
                ideasRemaining = Remaining.NO;
            } else {
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
        Cursor query = wdb.query(Database.RecipientsTable.TABLE_NAME, new String[]{Database.RecipientsTable._ID}, Database.RecipientsTable.NAME + "=?", new String[]{recipientName}, null, null, null);
        if (query.moveToFirst()) {
            long recipientId = query.getLong(0);
            long ideasCount = DatabaseUtils.queryNumEntries(wdb, Database.IdeasTable.TABLE_NAME, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{String.valueOf(recipientId)});
            return ideasCount == 0 ? Remaining.NO : Remaining.YES;
        }
        return Remaining.NO;
    }

    public enum Remaining {
        YES, NO
    }
}
