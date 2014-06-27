package com.sgrailways.giftidea;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.sgrailways.giftidea.db.Recipients;
import com.sgrailways.giftidea.wiring.BaseActivity;

import javax.inject.Inject;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.IDEAS_COUNT;
import static com.sgrailways.giftidea.db.Database.RecipientsTable.NAME;

public class RecipientsList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int RECIPIENTS_LOADER = 111;
    private CursorAdapter adapter;
    @Inject Session session;
    @Inject Recipients recipients;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity)getActivity()).inject(this);
        getLoaderManager().initLoader(RECIPIENTS_LOADER, null, this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new RecipientsCursorAdapter(getActivity());
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

    class RecipientsCursorAdapter extends CursorAdapter {
        private final LayoutInflater inflater;

        public RecipientsCursorAdapter(Context context) {
            super(context, null, 0);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = inflater.inflate(R.layout.recipient_item, viewGroup, false);
            ViewHolder holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.ideasCount = (TextView) view.findViewById(R.id.ideas_count);
            holder.ideasLabel = (TextView) view.findViewById(R.id.ideas_count_label);
            view.setTag(holder);
            return view;
        }

        @Override public void bindView(View view, Context context, Cursor cursor) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.name.setText(cursor.getString(1));
            long ideasCount = cursor.getLong(2);
            holder.ideasCount.setText(String.valueOf(ideasCount));
            holder.ideasLabel.setText(ideasCount == 1L ? R.string.idea_label : R.string.ideas_label);
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(RecipientsList.this.getActivity(), RecipientIdeasActivity.class);
                    String name = holder.name.getText().toString();
                    session.setActiveRecipient(recipients.findByName(name));
                    startActivity(intent);
                }
            });
        }
    }

    static class ViewHolder {
        TextView name;
        TextView ideasCount;
        TextView ideasLabel;
    }
}
