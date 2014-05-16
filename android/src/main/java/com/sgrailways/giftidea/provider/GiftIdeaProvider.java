package com.sgrailways.giftidea.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.sgrailways.giftidea.db.Database;
import com.sgrailways.giftidea.db.Ideas;
import com.sgrailways.giftidea.db.Recipients;

public class GiftIdeaProvider extends ContentProvider {
    private Database helper;
    private static final int RECIPIENTS_LIST = 1;
    private static final int RECIPIENT_ID = 2;
    private static final int IDEAS_LIST = 3;
    private static final int IDEA_ID = 4;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String AUTHORITY = "com.sgrailways.giftidea";

    static {
        URI_MATCHER.addURI(AUTHORITY, "recipients", RECIPIENTS_LIST);
        URI_MATCHER.addURI(AUTHORITY, "recipients/#", RECIPIENT_ID);
        URI_MATCHER.addURI(AUTHORITY, "ideas", IDEAS_LIST);
        URI_MATCHER.addURI(AUTHORITY, "ideas/#", IDEA_ID);
    }

    @Override public boolean onCreate() {
        helper = new Database(getContext());
        return true;
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch(URI_MATCHER.match(uri)) {
            case RECIPIENTS_LIST:
                builder.setTables(Database.RecipientsTable.TABLE_NAME);
                if(TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Database.RecipientsTable.NAME + " asc";
                }
                break;
            case RECIPIENT_ID:
                builder.setTables(Database.RecipientsTable.TABLE_NAME);
                builder.appendWhere(Database.RecipientsTable._ID + " = " + uri.getLastPathSegment());
                break;
            case IDEAS_LIST:
                builder.setTables(Database.IdeasTable.TABLE_NAME);
                break;
            case IDEA_ID:
                builder.setTables(Database.IdeasTable.TABLE_NAME);
                builder.appendWhere(Database.IdeasTable._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Cursor cursor = builder.query(db, projection, selection, args, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override public String getType(Uri uri) {
        switch(URI_MATCHER.match(uri)) {
            case RECIPIENTS_LIST:
                return Recipients.CONTENT_TYPE;
            case RECIPIENT_ID:
                return Recipients.CONTENT_RECIPIENT_TYPE;
            case IDEAS_LIST:
                return Ideas.CONTENT_TYPE;
            case IDEA_ID:
                return Ideas.CONTENT_IDEA_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported Content Type: " + uri);
        }
    }

    @Override public Uri insert(Uri uri, ContentValues contentValues) {
        if(URI_MATCHER.match(uri) != RECIPIENTS_LIST && URI_MATCHER.match(uri) != IDEAS_LIST) {
            throw new IllegalArgumentException("Unsupported URI for inserting: " + uri);
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        long id;
        if(URI_MATCHER.match(uri) == RECIPIENTS_LIST) {
            id = db.insert(Database.RecipientsTable.TABLE_NAME, null, contentValues);
        } else {
            id = db.insert(Database.IdeasTable.TABLE_NAME, null, contentValues);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uriFromId(id, uri);
    }

    @Override public int delete(Uri uri, String selection, String[] args) {
        int deleteCount;
        SQLiteDatabase db = helper.getWritableDatabase();
        switch(URI_MATCHER.match(uri)) {
            case RECIPIENTS_LIST:
                deleteCount = db.delete(Database.RecipientsTable.TABLE_NAME, selection, args);
                break;
            case RECIPIENT_ID:
                String idStr = uri.getLastPathSegment();
                String where = Database.RecipientsTable._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                deleteCount = db.delete(Database.RecipientsTable.TABLE_NAME, where, args);
                break;
            case IDEAS_LIST:
                deleteCount = db.delete(Database.IdeasTable.TABLE_NAME, selection, args);
                break;
            case IDEA_ID:
                String ideaId = uri.getLastPathSegment();
                String ideaWhere = Database.IdeasTable._ID + " = " + ideaId;
                if (!TextUtils.isEmpty(selection)) {
                    ideaWhere += " AND " + selection;
                }
                deleteCount = db.delete(Database.IdeasTable.TABLE_NAME, ideaWhere, args);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Delete URI: " + uri);
        }
        if(deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteCount;
    }

    @Override public int update(Uri uri, ContentValues contentValues, String selection, String[] args) {
        int updateCount;
        SQLiteDatabase db = helper.getWritableDatabase();
        switch(URI_MATCHER.match(uri)) {
            case RECIPIENTS_LIST:
                updateCount = db.update(Database.RecipientsTable.TABLE_NAME, contentValues, selection, args);
                break;
            case RECIPIENT_ID:
                String idStr = uri.getLastPathSegment();
                String where = Database.RecipientsTable._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(Database.RecipientsTable.TABLE_NAME, contentValues, where, args);
                break;
            case IDEAS_LIST:
                updateCount = db.update(Database.IdeasTable.TABLE_NAME, contentValues, selection, args);
                break;
            case IDEA_ID:
                String ideaId = uri.getLastPathSegment();
                String ideaWhere = Database.IdeasTable._ID + " = " + ideaId;
                if (!TextUtils.isEmpty(selection)) {
                    ideaWhere += " AND " + selection;
                }
                updateCount = db.update(Database.IdeasTable.TABLE_NAME, contentValues, ideaWhere, args);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Update URI: " + uri);
        }
        if(updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    private Uri uriFromId(long id, Uri uri) {
        if(id > 0) {
            Uri uriWithId = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(uriWithId, null);
            return uriWithId;
        }
        throw new SQLException("Problem inserting into uri: " + uri);
    }
}
