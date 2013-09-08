package com.sgrailways.giftidea.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Database extends SQLiteOpenHelper {
    private final static String NAME = "giftidea.db";
    private final static int VERSION = 2;

    @Inject
    public Database(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        String createRecipients = new StringBuilder("create table ").append(RecipientsTable.TABLE_NAME).append("(")
                .append(RecipientsTable._ID).append(" integer primary key autoincrement,")
                .append(RecipientsTable.NAME).append(" text,")
                .append(RecipientsTable.IDEAS_COUNT).append(" integer,")
                .append(RecipientsTable.CREATED_AT).append(" text,")
                .append(RecipientsTable.UPDATED_AT).append(" text)").toString();
        String createIdeas = new StringBuilder("create table ").append(IdeasTable.TABLE_NAME).append("(")
                .append(IdeasTable._ID).append(" integer primary key autoincrement,")
                .append(IdeasTable.RECIPIENT_ID).append(" integer,")
                .append(IdeasTable.IDEA).append(" text,")
                .append(IdeasTable.IS_DONE).append(" text,")
                .append(IdeasTable.CREATED_AT).append(" text,")
                .append(IdeasTable.UPDATED_AT).append(" text)").toString();

        db.execSQL(createRecipients);
        db.execSQL(createIdeas);
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS IDX_RECIPIENTS_NAME ON " + RecipientsTable.TABLE_NAME + "(" + RecipientsTable.NAME + ")");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2) {
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS IDX_RECIPIENTS_NAME ON " + RecipientsTable.TABLE_NAME + " (" + RecipientsTable.NAME + ")");
            db.execSQL("ALTER TABLE " + RecipientsTable.TABLE_NAME + " ADD COLUMN " + RecipientsTable.IDEAS_COUNT + " integer");
            Cursor cursor = db.query(RecipientsTable.TABLE_NAME, new String[]{RecipientsTable._ID}, null, null, null, null, null);
            if(cursor.moveToFirst()) {
                while(!cursor.isAfterLast()) {
                    long recipientId = cursor.getLong(0);
                    String initializeCount = new StringBuilder("UPDATE ").append(RecipientsTable.TABLE_NAME)
                            .append(" SET ").append(RecipientsTable.IDEAS_COUNT).append("=(SELECT COUNT(*) FROM ")
                            .append(IdeasTable.TABLE_NAME).append(" WHERE ")
                            .append(IdeasTable.RECIPIENT_ID).append("=").append(recipientId).append(") WHERE ")
                            .append(RecipientsTable._ID).append("=").append(recipientId).toString();
                    db.execSQL(initializeCount);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
    }

    public static class RecipientsTable implements BaseColumns {
        public final static String TABLE_NAME = "recipients";
        public final static String NAME = "name";
        public final static String IDEAS_COUNT = "ideas_count";
        public final static String CREATED_AT = "created_at";
        public final static String UPDATED_AT = "updated_at";
    }

    public static class IdeasTable implements BaseColumns {
        public final static String TABLE_NAME = "ideas";
        public final static String RECIPIENT_ID = "recipient_id";
        public final static String IDEA = "idea";
        public final static String IS_DONE = "is_done";
        public final static String CREATED_AT = "created_at";
        public final static String UPDATED_AT = "updated_at";
    }
}
