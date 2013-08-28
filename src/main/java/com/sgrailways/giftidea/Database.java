package com.sgrailways.giftidea;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Database extends SQLiteOpenHelper {
    private final static String NAME = "giftidea.db";
    private final static int VERSION = 1;

    @Inject
    public Database(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        String createRecipients = new StringBuilder("create table ").append(RecipientsTable.TABLE_NAME).append("(")
                .append(RecipientsTable._ID).append(" integer primary key autoincrement,")
                .append(RecipientsTable.NAME).append(" text,")
                .append(RecipientsTable.CREATED_AT).append(" text,")
                .append(RecipientsTable.UPDATED_AT).append(" text)").toString();
        String createIdeas = new StringBuilder("create table ").append(IdeasTable.TABLE_NAME).append("(")
                .append(IdeasTable._ID).append(" integer primary key autoincrement,")
                .append(IdeasTable.RECIPIENT_ID).append(" integer,")
                .append(IdeasTable.IDEA).append(" text,")
                .append(IdeasTable.CREATED_AT).append(" text,")
                .append(IdeasTable.UPDATED_AT).append(" text)").toString();

        db.execSQL(createRecipients);
        db.execSQL(createIdeas);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    static class RecipientsTable implements BaseColumns {
        public final static String TABLE_NAME = "recipients";
        public final static String NAME = "name";
        public final static String CREATED_AT = "created_at";
        public final static String UPDATED_AT = "updated_at";
    }

    static class IdeasTable implements BaseColumns {
        public final static String TABLE_NAME = "ideas";
        public final static String RECIPIENT_ID = "recipient_id";
        public final static String IDEA = "idea";
        public final static String CREATED_AT = "created_at";
        public final static String UPDATED_AT = "updated_at";
    }
}
