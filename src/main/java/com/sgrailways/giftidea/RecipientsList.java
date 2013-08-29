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
import android.widget.TextView;
import com.google.inject.Inject;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectResource;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.Database.RecipientsTable.NAME;
import static com.sgrailways.giftidea.Database.RecipientsTable.TABLE_NAME;

public class RecipientsList extends RoboListFragment {
    @Inject Database database;
    @InjectResource(com.sgrailways.R.string.no_recipients_message) String noRecipientsMessage;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListAdapter adapter = new SimpleCursorAdapter(
                this.getActivity(),
                R.layout.simple_list_item_1,
                cursor(),
                new String[]{NAME},
                new int[]{R.id.text1}
        );
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onResume() {
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(cursor());
        setEmptyText(noRecipientsMessage);
        getActivity().setTitle(com.sgrailways.R.string.gift_recipients_title);
        super.onResume();
    }

    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this.getActivity(), RecipientIdeasActivity.class);
        intent.putExtra("recipient", ((TextView) v).getText().toString());
        startActivity(intent);
    }

    private Cursor cursor() {
        SQLiteDatabase rdb = database.getReadableDatabase();
        return rdb.query(TABLE_NAME, new String[]{_ID, NAME}, null, null, null, null, NAME + " ASC");
    }
}
