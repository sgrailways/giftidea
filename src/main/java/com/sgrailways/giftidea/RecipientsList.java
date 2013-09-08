package com.sgrailways.giftidea;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.inject.Inject;
import com.sgrailways.giftidea.db.Database;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectResource;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.IDEAS_COUNT;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.NAME;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.TABLE_NAME;

public class RecipientsList extends RoboListFragment {
    @Inject Database database;
    @InjectResource(com.sgrailways.giftidea.R.string.no_recipients_message) String noRecipientsMessage;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this.getActivity(),
                R.layout.recipient_item,
                cursor(),
                new String[]{NAME},
                new int[]{R.id.name}
        );
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                View rootView = view.getRootView();
                TextView name = (TextView) rootView.findViewById(R.id.name);
                TextView count = (TextView) rootView.findViewById(R.id.ideas_count);
                if (name == null || count == null) {
                    return false;
                }
                name.setText(cursor.getString(1));
                count.setText(String.valueOf(cursor.getLong(2)));
                rootView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(RecipientsList.this.getActivity(), RecipientIdeasActivity.class);
                        intent.putExtra("recipient", ((TextView) v.findViewById(R.id.name)).getText().toString());
                        startActivity(intent);
                    }
                });
                return true;
            }
        });
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onResume() {
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(cursor());
        setEmptyText(noRecipientsMessage);
        getActivity().setTitle(com.sgrailways.giftidea.R.string.gift_recipients_title);
        super.onResume();
    }

    private Cursor cursor() {
        SQLiteDatabase rdb = database.getWritableDatabase();  // when this was readable, database onUpgrade didn't work
        return rdb.query(TABLE_NAME, new String[]{_ID, NAME, IDEAS_COUNT}, null, null, null, null, NAME + " ASC");
    }
}
