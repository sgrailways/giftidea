package com.sgrailways.giftidea;

import android.app.AlertDialog;
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
import com.sgrailways.giftidea.listeners.DialogDismissListener;
import com.squareup.otto.Bus;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectResource;

import static com.sgrailways.giftidea.db.Database.IdeasTable.IDEA;

public class RecipientIdeasList extends RoboListFragment {
    @Inject Bus bus;
    @Inject Ideas ideas;
    @Inject Toaster toaster;
    @Inject DialogDismissListener dialogDismissListener;
    @Inject ListenerFactory listenerFactory;
    @InjectResource(R.string.app_name) String appName;
    @InjectResource(R.string.got_it_message) String gotItMessage;
    @InjectResource(R.string.finished_idea_deleted_message) String deletedMessage;
    @InjectExtra("recipient") String recipientName;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this.getActivity(),
                R.layout.idea_item,
                ideas.findAllForRecipientName(recipientName),
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
                if (Boolean.parseBoolean(cursor.getString(2))) {
                    gotIt.setVisibility(View.GONE);
                    idea.setPaintFlags(idea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    idea.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(RecipientIdeasList.this.getActivity())
                                    .setNegativeButton(R.string.cancel, dialogDismissListener)
                                    .setPositiveButton(R.string.delete, listenerFactory.deleteIdeaListener(id, deletedMessage))
                                    .setTitle(R.string.confirmation)
                                    .setMessage("Delete idea for " + recipientName + "?");
                            alert.create().show();
                        }
                    });
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
        Ideas.Remaining remaining = ideas.forRecipient(recipientName);
        if (remaining == Ideas.Remaining.YES) {
            getActivity().setTitle(recipientName + " " + appName);
        } else if (remaining == Ideas.Remaining.NO) {
            getActivity().finish();
        }
    }
}
