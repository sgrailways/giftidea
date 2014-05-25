package com.sgrailways.giftidea.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import com.sgrailways.giftidea.core.domain.Holiday;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

import javax.inject.Inject;

public class Holidays {
    private final Database helper;

    @Inject
    public Holidays(Database helper) {
        this.helper = helper;
    }

    public Holiday findNext() {
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Database.HolidaysTable.TABLE_NAME);
        builder.appendWhere(Database.HolidaysTable.CELEBRATED_AT + ">" + LocalDate.now().toString(ISODateTimeFormat.basicDate()));
        Cursor cursor= builder.query(db, new String[]{Database.HolidaysTable._ID, Database.HolidaysTable.NAME, Database.HolidaysTable.CELEBRATED_AT}, null, null, null, null, null, "1");
        if(!cursor.moveToFirst()) {
            return null;
        }
        try {
            return new Holiday(cursor.getLong(0), null, cursor.getString(1), LocalDate.parse(cursor.getString(2), ISODateTimeFormat.basicDate()), true, null, null);
        } finally {
            cursor.close();
        }
    }

    public Holiday findById(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Database.HolidaysTable.TABLE_NAME);
        builder.appendWhere(Database.HolidaysTable._ID + "=" + id);
        Cursor cursor= builder.query(db, new String[]{Database.HolidaysTable.NAME, Database.HolidaysTable.CELEBRATED_AT}, null, null, null, null, null, "1");
        if(!cursor.moveToFirst()) {
            return null;
        }
        try {
            return new Holiday(null, cursor.getString(0), LocalDate.parse(cursor.getString(1), ISODateTimeFormat.basicDate()), true, null, null);
        } finally {
            cursor.close();
        }
    }
}
