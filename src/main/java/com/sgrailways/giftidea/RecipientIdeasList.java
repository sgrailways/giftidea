package com.sgrailways.giftidea;

import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.inject.Inject;
import com.sgrailways.giftidea.db.Ideas;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectResource;

import static com.sgrailways.giftidea.db.Database.IdeasTable.IDEA;

public class RecipientIdeasList extends RoboListFragment {
    @Inject Ideas ideas;
    @Inject ListenerFactory listenerFactory;
    @Inject Session session;
    @InjectResource(R.string.app_name) String appName;
    @InjectResource(R.string.got_it_message) String gotItMessage;
    @InjectResource(R.string.finished_idea_deleted_message) String deletedMessage;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this.getActivity(),
                R.layout.idea_item,
                ideas.findAllForRecipientName(session.getRecipientName()),
                new String[]{IDEA},
                new int[]{R.id.idea}
        );
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView idea = (TextView) view.getRootView().findViewById(R.id.idea);
                TextView gotIt = (TextView) view.getRootView().findViewById(R.id.got_it);
                if (gotIt == null || idea == null) {
                    return false;
                }
                final long id = cursor.getLong(0);
                idea.setText(cursor.getString(1));
                String recipientName = session.getRecipientName();
                if (Boolean.parseBoolean(cursor.getString(2))) {
                    gotIt.setVisibility(View.GONE);
                    idea.setPaintFlags(idea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    idea.setOnClickListener(listenerFactory.confirmDeleteListener(id, recipientName, deletedMessage, getActivity()));
                } else {
                    gotIt.setOnClickListener(listenerFactory.gotItListener(id, recipientName, gotItMessage));
                    idea.setOnClickListener(listenerFactory.editIdeaListener(id, recipientName));
                }
                return true;
            }
        });
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onResume() {
        super.onResume();
        String recipientName = session.getRecipientName();
        Ideas.Remaining remaining = ideas.forRecipient(recipientName);
        if (remaining == Ideas.Remaining.YES) {
            getActivity().setTitle(recipientName + " " + appName);
        } else if (remaining == Ideas.Remaining.NO) {
            getActivity().finish();
        }
    }
}
