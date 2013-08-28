package com.sgrailways.giftidea;

import android.R;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import com.google.inject.Inject;
import roboguice.fragment.RoboListFragment;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.Database.RecipientsTable.NAME;
import static com.sgrailways.giftidea.Database.RecipientsTable.TABLE_NAME;

public class RecipientsList extends RoboListFragment {
    @Inject Database database;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SQLiteDatabase rdb = database.getReadableDatabase();
        Cursor cursor = rdb.query(TABLE_NAME, new String[]{_ID, NAME}, null, null, null, null, NAME + " ASC");
        ListAdapter adapter = new SimpleCursorAdapter(
                this.getActivity(),
                R.layout.simple_list_item_1,
                cursor,
                new String[]{NAME},
                new int[]{R.id.text1}
        );
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onResume() {
        SQLiteDatabase rdb = database.getReadableDatabase();
        ((SimpleCursorAdapter)getListAdapter()).swapCursor(rdb.query(TABLE_NAME, new String[]{_ID, NAME}, null, null, null, null, NAME + " ASC"));
        super.onResume();
    }
}
