package com.sgrailways.giftidea.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.inject.Inject;
import com.sgrailways.giftidea.domain.MissingRecipient;
import com.sgrailways.giftidea.domain.Recipient;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.*;

public class Recipients {
    private final static String[] COLUMNS = new String[]{_ID, NAME, IDEAS_COUNT};
    private SQLiteDatabase writeableDatabase;

    @Inject
    public Recipients(Database database) {
        this.writeableDatabase = database.getWritableDatabase();
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
}
