package com.sgrailways.giftidea;

import android.R;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.google.inject.Inject;
import roboguice.fragment.RoboListFragment;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.Database.IdeasTable.IDEA;
import static com.sgrailways.giftidea.Database.IdeasTable.TABLE_NAME;

public class RecipientIdeasList extends RoboListFragment {
    @Inject Database database;
    private String recipientName;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.recipientName = getActivity().getIntent().getExtras().getString("recipient");
        ListAdapter adapter = new SimpleCursorAdapter(
                this.getActivity(),
                R.layout.simple_list_item_1,
                cursor(),
                new String[]{IDEA},
                new int[]{R.id.text1}
        );
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onResume() {
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(cursor());
        super.onResume();
    }

    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this.getActivity(), IdeaActivity.class);
        intent.putExtra("ideaId", id);
        intent.putExtra("recipient", recipientName);
        startActivity(intent);
    }

    private Cursor cursor() {
        SQLiteDatabase rdb = database.getReadableDatabase();
        Cursor cursor = rdb.query(Database.RecipientsTable.TABLE_NAME, new String[]{_ID}, Database.RecipientsTable.NAME + "=?", new String[]{recipientName}, null, null, null, "1");
        String recipientId = "";
        if (cursor.moveToFirst()) {
            recipientId = cursor.getString(0);
        }
        return rdb.query(TABLE_NAME, new String[]{_ID, IDEA}, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{recipientId}, null, null, IDEA + " ASC");
    }
}
