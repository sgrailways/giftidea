package com.sgrailways.giftidea;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.inject.Inject;
import com.sgrailways.giftidea.db.Recipients;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectResource;

import static com.sgrailways.giftidea.db.Database.RecipientsTable.NAME;

public class RecipientsList extends RoboListFragment {
    @Inject Recipients recipients;
    @InjectResource(R.string.no_recipients_message) String noRecipientsMessage;
    @InjectResource(R.string.idea_label) String singularIdeaLabel;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this.getActivity(),
                R.layout.recipient_item,
                recipients.findAllOrderedByName(),
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
                long ideasCount = cursor.getLong(2);
                count.setText(String.valueOf(ideasCount));
                if(ideasCount == 1L) {
                    TextView ideasLabel = (TextView) rootView.findViewById(R.id.ideas_count_label);
                    ideasLabel.setText(singularIdeaLabel);
                }
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
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(recipients.findAllOrderedByName());
        setEmptyText(noRecipientsMessage);
        getActivity().setTitle(com.sgrailways.giftidea.R.string.gift_recipients_title);
        super.onResume();
    }
}
