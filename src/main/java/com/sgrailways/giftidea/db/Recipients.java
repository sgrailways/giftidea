package com.sgrailways.giftidea.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.inject.Inject;import com.sgrailways.giftidea.ProgrammerErrorException;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.NAME;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.TABLE_NAME;

public class Recipients {
    private SQLiteDatabase writeableDatabase;

    @Inject
    public Recipients(Database database) {
        this.writeableDatabase = database.getWritableDatabase();
    }

    public long findIdByName(String name) {
        Cursor cursor = writeableDatabase.query(TABLE_NAME, new String[]{_ID}, NAME + "=?", new String[]{name}, null, null, null, "1");
        if(cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        throw new ProgrammerErrorException();
    }
}
