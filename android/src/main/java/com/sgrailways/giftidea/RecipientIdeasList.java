package com.sgrailways.giftidea;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.sgrailways.giftidea.core.domain.Recipient;
import com.sgrailways.giftidea.db.Database;
import com.sgrailways.giftidea.db.Ideas;
import com.sgrailways.giftidea.wiring.BaseActivity;

import javax.inject.Inject;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.IdeasTable.IDEA;

public class RecipientIdeasList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int IDEAS_LOADER = 112;
    private SimpleCursorAdapter adapter;
    @Inject ListenerFactory listenerFactory;
    @Inject Session session;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).inject(this);
        getLoaderManager().initLoader(IDEAS_LOADER, null, this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.idea_item,
                null,
                new String[]{IDEA},
                new int[]{R.id.idea},
                0
        );
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView idea = (TextView) view.getRootView().findViewById(R.id.idea);
                TextView gotIt = (TextView) view.getRootView().findViewById(R.id.got_it);
                if (gotIt == null || idea == null) {
                    return false;
                }
                final long id = cursor.getLong(0);
                view.setId((int)id);
                String ideaString = cursor.getString(1);
                idea.setText(ideaString);
                Recipient recipient = session.getActiveRecipient();
                if (Boolean.parseBoolean(cursor.getString(2))) {
                    gotIt.setVisibility(View.GONE);
                    idea.setPaintFlags(idea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    idea.setOnClickListener(listenerFactory.confirmDeleteListener(id, recipient, getString(R.string.finished_idea_deleted_message), getActivity()));
                } else {
                    gotIt.setOnClickListener(listenerFactory.gotItListener(id, recipient, getString(R.string.got_it_message)));
                    idea.setOnClickListener(listenerFactory.editIdeaListener(id, recipient));
                }
                return true;
            }
        });
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onResume() {
        super.onResume();
        getActivity().setTitle(session.getActiveRecipientName() + " " + getString(R.string.app_name));
    }

    @Override public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case IDEAS_LOADER:
                return new CursorLoader(getActivity(), Ideas.URI, Ideas.COLUMNS, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{session.getActiveRecipientId()}, _ID + " ASC");
            default:
                return null;
        }
    }

    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {}
}
