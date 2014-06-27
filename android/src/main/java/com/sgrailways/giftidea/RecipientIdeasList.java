package com.sgrailways.giftidea;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.sgrailways.giftidea.core.domain.Recipient;
import com.sgrailways.giftidea.db.Database;
import com.sgrailways.giftidea.db.Ideas;
import com.sgrailways.giftidea.wiring.BaseActivity;

import javax.inject.Inject;

import static android.provider.BaseColumns._ID;

public class RecipientIdeasList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int IDEAS_LOADER = 112;
    private CursorAdapter adapter;
    @Inject ListenerFactory listenerFactory;
    @Inject Session session;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).inject(this);
        getLoaderManager().initLoader(IDEAS_LOADER, null, this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new IdeasCursorAdapter(getActivity());
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
                String sortOrder = String.format("%s ASC, %s ASC", Database.IdeasTable.IS_DONE, _ID);
                return new CursorLoader(getActivity(), Ideas.URI, Ideas.COLUMNS, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{session.getActiveRecipientId()}, sortOrder);
            default:
                return null;
        }
    }

    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    class IdeasCursorAdapter extends CursorAdapter {
        private final LayoutInflater inflater;

        public IdeasCursorAdapter(Context context) {
            super(context, null, 0);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            ViewHolder holder = new ViewHolder();
            View view = inflater.inflate(R.layout.idea_item, viewGroup, false);
            holder.idea = (TextView) view.findViewById(R.id.idea);
            holder.gotIt = (TextView) view.findViewById(R.id.got_it);
            view.setTag(holder);
            return view;
        }

        @Override public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.idea.setText(cursor.getString(1));
            Recipient recipient = session.getActiveRecipient();
            final long id = cursor.getLong(0);
            view.setId((int) id);
            boolean done = Boolean.parseBoolean(cursor.getString(2));
            holder.gotIt.setVisibility(done ? View.GONE : View.VISIBLE);
            if (done) {
                holder.idea.setPaintFlags(holder.idea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.idea.setOnClickListener(listenerFactory.confirmDeleteListener(id, recipient, getString(R.string.finished_idea_deleted_message), getActivity()));
            } else {
                holder.gotIt.setOnClickListener(listenerFactory.gotItListener(id, recipient, getString(R.string.got_it_message)));
                holder.idea.setOnClickListener(listenerFactory.editIdeaListener(id, recipient));
                holder.idea.setPaintFlags(holder.idea.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }

    static class ViewHolder {
        TextView idea;
        TextView gotIt;
    }
}
