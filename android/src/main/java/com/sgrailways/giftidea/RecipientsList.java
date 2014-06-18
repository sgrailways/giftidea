package com.sgrailways.giftidea;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sgrailways.giftidea.db.Recipients;
import com.sgrailways.giftidea.wiring.BaseActivity;

import javax.inject.Inject;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.IDEAS_COUNT;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.NAME;

public class RecipientsList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int RECIPIENTS_LOADER = 111;
    private SimpleCursorAdapter adapter;
    @Inject Session session;
    @Inject Recipients recipients;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity)getActivity()).inject(this);
        getLoaderManager().initLoader(RECIPIENTS_LOADER, null, this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.recipient_item,
                null,
                new String[]{NAME, IDEAS_COUNT},
                new int[]{R.id.name, R.id.ideas_count},
                0
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
                long ideasCount = cursor.getLong(2);
                count.setText(String.valueOf(ideasCount));
                TextView ideasLabel = (TextView) rootView.findViewById(R.id.ideas_count_label);
                if(ideasCount == 1L) {
                    ideasLabel.setText(getString(R.string.idea_label));
                } else {
                    ideasLabel.setText(R.string.ideas_label);
                }
                rootView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(RecipientsList.this.getActivity(), RecipientIdeasActivity.class);
                        String name = ((TextView) v.findViewById(R.id.name)).getText().toString();
                        session.setActiveRecipient(recipients.findByName(name));
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
        setEmptyText(getString(R.string.no_recipients_message));
        getActivity().setTitle(R.string.gift_recipients_title);
        super.onResume();
    }

    @Override public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case RECIPIENTS_LOADER:
                return new CursorLoader(getActivity(), Recipients.URI, new String[]{_ID, NAME, IDEAS_COUNT}, null, null, null);
            default:
                return null;
        }
    }

    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {}
}
