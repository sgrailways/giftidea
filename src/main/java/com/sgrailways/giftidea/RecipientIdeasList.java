package com.sgrailways.giftidea;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.inject.Inject;
import com.sgrailways.giftidea.db.Database;
import com.sgrailways.giftidea.db.Ideas;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectResource;

import static android.provider.BaseColumns._ID;
import static com.sgrailways.giftidea.db.Database.IdeasTable.*;

public class RecipientIdeasList extends RoboListFragment {
    @Inject
    Database database;
    @InjectResource(R.string.app_name) String appName;
    private String recipientName;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.recipientName = getActivity().getIntent().getExtras().getString("recipient");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this.getActivity(),
                R.layout.idea_item,
                cursor(),
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
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            new Ideas(database).delete(id);
                                            Toast.makeText(RecipientIdeasList.this.getActivity(), R.string.finished_idea_deleted_message, Toast.LENGTH_SHORT).show();
                                            ((RecipientIdeasActivity) getActivity()).onResume();
                                        }
                                    })
                                    .setTitle(R.string.confirmation)
                                    .setMessage("Delete idea for " + recipientName + "?");
                            alert.create().show();
                        }
                    });
                } else {
                    gotIt.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            SQLiteDatabase wdb = database.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(Database.IdeasTable.IS_DONE, String.valueOf(true));
                            values.put(Database.IdeasTable.UPDATED_AT, DateTime.now().toString(ISODateTimeFormat.basicDateTime()));
                            wdb.update(Database.IdeasTable.TABLE_NAME, values, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(id)});

                            Cursor query = wdb.query(Database.RecipientsTable.TABLE_NAME, new String[]{Database.RecipientsTable._ID, Database.RecipientsTable.IDEAS_COUNT}, Database.RecipientsTable.NAME + "=?", new String[]{recipientName}, null, null, null);
                            if(query.moveToFirst()) {
                                long recipientId = query.getLong(0);
                                long ideasCount = query.getLong(1);
                                ContentValues recipientValues = new ContentValues();
                                recipientValues.put(Database.RecipientsTable.IDEAS_COUNT, --ideasCount);
                                wdb.update(Database.RecipientsTable.TABLE_NAME, recipientValues, Database.RecipientsTable._ID + "=?", new String[]{String.valueOf(recipientId)});
                            }

                            Toast.makeText(RecipientIdeasList.this.getActivity(), R.string.got_it_message, Toast.LENGTH_SHORT).show();
                            ((RecipientIdeasActivity) getActivity()).onResume();
                        }
                    });
                    idea.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(RecipientIdeasList.this.getActivity(), IdeaActivity.class);
                            intent.putExtra("ideaId", id);
                            intent.putExtra("recipient", recipientName);
                            startActivity(intent);
                        }
                    });
                }
                return true;
            }
        });
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onResume() {
        super.onResume();
        Ideas.Remaining remaining = new Ideas(database).forRecipient(recipientName);
        if (remaining == Ideas.Remaining.YES) {
            getActivity().setTitle(recipientName + " " + appName);
        } else if (remaining == Ideas.Remaining.NO) {
            getActivity().finish();
        }
    }

    private Cursor cursor() {
        SQLiteDatabase rdb = database.getReadableDatabase();
        Cursor cursor = rdb.query(Database.RecipientsTable.TABLE_NAME, new String[]{_ID}, Database.RecipientsTable.NAME + "=?", new String[]{recipientName}, null, null, null, "1");
        String recipientId = "";
        if (cursor.moveToFirst()) {
            recipientId = cursor.getString(0);
        }
        return rdb.query(TABLE_NAME, new String[]{_ID, IDEA, IS_DONE}, Database.IdeasTable.RECIPIENT_ID + "=?", new String[]{recipientId}, null, null, IS_DONE + " ASC");
    }
}
