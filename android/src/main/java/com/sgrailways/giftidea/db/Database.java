package com.sgrailways.giftidea.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.text.TextUtils;
import com.sgrailways.giftidea.core.domain.Holiday;
import com.sgrailways.giftidea.wiring.ForApplication;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

import static java.util.Locale.US;
import static org.joda.time.DateTimeZone.UTC;

@Singleton
public class Database extends SQLiteOpenHelper {
    private final static String NAME = "giftidea.db";
    private final static int VERSION = 4;

    @Inject
    public Database(@ForApplication Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        String createRecipients = new StringBuilder("create table ").append(RecipientsTable.TABLE_NAME).append("(")
                .append(RecipientsTable._ID).append(" integer primary key autoincrement,")
                .append(RecipientsTable.NAME).append(" text,")
                .append(RecipientsTable.IDEAS_COUNT).append(" integer,")
                .append(RecipientsTable.CREATED_AT).append(" text,")
                .append(RecipientsTable.UPDATED_AT).append(" text)").toString();

        db.execSQL(createRecipients);
        db.execSQL(IdeasTable.CREATE_TABLE);
        db.execSQL(HolidaysTable.CREATE_STATEMENT);
        bootstrapHolidays(db);
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS IDX_RECIPIENTS_NAME ON " + RecipientsTable.TABLE_NAME + "(" + RecipientsTable.NAME + ")");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.i("Upgrading database from %d to %d", oldVersion, newVersion);
        if(oldVersion == 1) {
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS IDX_RECIPIENTS_NAME ON " + RecipientsTable.TABLE_NAME + " (" + RecipientsTable.NAME + ")");
            db.execSQL("ALTER TABLE " + RecipientsTable.TABLE_NAME + " ADD COLUMN " + RecipientsTable.IDEAS_COUNT + " integer");
            Cursor cursor = db.query(RecipientsTable.TABLE_NAME, new String[]{RecipientsTable._ID}, null, null, null, null, null);
            if(cursor.moveToFirst()) {
                while(!cursor.isAfterLast()) {
                    long recipientId = cursor.getLong(0);
                    String initializeCount = new StringBuilder("UPDATE ").append(RecipientsTable.TABLE_NAME)
                            .append(" SET ").append(RecipientsTable.IDEAS_COUNT).append("=(SELECT COUNT(*) FROM ")
                            .append(IdeasTable.TABLE_NAME).append(" WHERE ")
                            .append(IdeasTable.RECIPIENT_ID).append("=").append(recipientId)
                            .append(" AND ").append(IdeasTable.IS_DONE).append("='false'").append(") WHERE ")
                            .append(RecipientsTable._ID).append("=").append(recipientId).toString();
                    db.execSQL(initializeCount);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        if(oldVersion == 1 || oldVersion == 2) {
            db.execSQL(HolidaysTable.CREATE_STATEMENT);
            bootstrapHolidays(db);
        }
        if (oldVersion <= 3) {
            db.execSQL("ALTER TABLE " + IdeasTable.TABLE_NAME + " ADD COLUMN " + IdeasTable.IMAGE_URI + " TEXT");
        }
    }

    public void upgrade() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.close();
    }

    private static void bootstrapHolidays(SQLiteDatabase db) {
        SQLiteStatement statement = db.compileStatement(HolidaysTable.INSERT_STATEMENT);
        DateTime now = DateTime.now(UTC);
        db.beginTransaction();
        for(Holiday holiday : Arrays.asList(
                // 2014
                new Holiday(US, "Father's Day", new LocalDate(2014, 6, 15), true, now, now),
                new Holiday(US, "Grandparents Day", new LocalDate(2014, 9, 7), true, now, now),
                new Holiday(US, "Programmers' Day", new LocalDate(2014, 9, 13), true, now, now),
                new Holiday(US, "Boss's Day", new LocalDate(2014, 10, 16), true, now, now),

                // 2015
                new Holiday(US, "Valentine's Day", new LocalDate(2015, 2, 14), true, now, now),
                new Holiday(US, "Administrative Professionals' Day", new LocalDate(2015, 4, 22), true, now, now),
                new Holiday(US, "Teacher Appreciation Day", new LocalDate(2015, 5, 5), true, now, now),
                new Holiday(US, "Siblings Day", new LocalDate(2015, 4, 10), true, now, now),
                new Holiday(US, "Mother's Day", new LocalDate(2015, 5, 10), true, now, now),
                new Holiday(US, "Father's Day", new LocalDate(2015, 6, 21), true, now, now),
                new Holiday(US, "Boss's Day", new LocalDate(2015, 10, 16), true, now, now),
                new Holiday(US, "Grandparents Day", new LocalDate(2015, 9, 13), true, now, now),
                new Holiday(US, "Programmers' Day", new LocalDate(2015, 9, 13), true, now, now),

                // 2016
                new Holiday(US, "Valentine's Day", new LocalDate(2016, 2, 14), true, now, now),
                new Holiday(US, "Administrative Professionals' Day", new LocalDate(2016, 4, 27), true, now, now),
                new Holiday(US, "Teacher Appreciation Day", new LocalDate(2016, 5, 3), true, now, now),
                new Holiday(US, "Siblings Day", new LocalDate(2016, 4, 10), true, now, now),
                new Holiday(US, "Mother's Day", new LocalDate(2016, 5, 8), true, now, now),
                new Holiday(US, "Father's Day", new LocalDate(2016, 6, 19), true, now, now),
                new Holiday(US, "Boss's Day", new LocalDate(2016, 10, 17), true, now, now),
                new Holiday(US, "Grandparents Day", new LocalDate(2016, 9, 11), true, now, now),
                new Holiday(US, "Programmers' Day", new LocalDate(2016, 9, 12), true, now, now)
        )) {
            statement.bindAllArgsAsStrings(new String[]{
                    holiday.getName(),
                    holiday.getLocale(),
                    String.valueOf(holiday.isCelebrated()),
                    holiday.getCelebratedAt(),
                    holiday.getCreatedAt(),
                    holiday.getUpdatedAt()
            });
            statement.executeInsert();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
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
        public final static String IMAGE_URI = "image_uri";
        public static final String CREATE_TABLE = "create table " + IdeasTable.TABLE_NAME + "(" + IdeasTable._ID + " integer primary key autoincrement," + IdeasTable.RECIPIENT_ID + " integer," + IdeasTable.IDEA + " text," + IdeasTable.IS_DONE + " text," + IdeasTable.IMAGE_URI + " text," + IdeasTable.CREATED_AT + " text," + IdeasTable.UPDATED_AT + " text)";
    }

    public static class HolidaysTable implements BaseColumns {
        public final static String TABLE_NAME = "holidays";
        public final static String LOCALE = "locale";
        public final static String NAME = "name";
        public final static String IS_CELEBRATED = "is_celebrated";
        public final static String CELEBRATED_AT = "celebrated_at";
        public final static String CREATED_AT = "created_at";
        public final static String UPDATED_AT = "updated_at";

        public final static String CREATE_STATEMENT = "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement," + NAME + " text," + LOCALE + " text," + IS_CELEBRATED + " text," + CELEBRATED_AT + " text," + CREATED_AT + " text," + UPDATED_AT + " text)";
        public final static String INSERT_STATEMENT = "insert into " + TABLE_NAME + "(" + TextUtils.join(",", Arrays.asList(NAME, LOCALE, IS_CELEBRATED, CELEBRATED_AT, CREATED_AT, UPDATED_AT)) + ") VALUES (?,?,?,?,?,?)";
    }
}
