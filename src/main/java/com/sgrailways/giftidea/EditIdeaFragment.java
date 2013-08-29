package com.sgrailways.giftidea;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import com.google.inject.Inject;
import com.sgrailways.R;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

public class EditIdeaFragment extends RoboFragment {
    @Inject Database database;
    @InjectView(R.id.idea) EditText idea;
    @InjectView(R.id.recipient) TextView recipient;
    @InjectResource(R.string.no_idea_message) String noIdeaMessage;
    boolean hasAppropriateLength = false;
    private long ideaId;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_idea, container, false);
    }

    @Override public void onResume() {
        super.onResume();
        Bundle extras = getActivity().getIntent().getExtras();
        ideaId = extras.getLong("ideaId", -1L);
        SQLiteDatabase rdb = database.getReadableDatabase();
        Cursor cursor = rdb.query(Database.IdeasTable.TABLE_NAME, new String[]{Database.IdeasTable._ID, Database.IdeasTable.IDEA}, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(ideaId)}, null, null, null, "1");
        cursor.moveToFirst();
        idea.setText(cursor.getString(1));
        hasAppropriateLength = StringUtils.isNotEmpty(idea.getText().toString());
        cursor.close();
        idea.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                hasAppropriateLength = StringUtils.isNotEmpty(idea.getText().toString());
            }
        });
        recipient.setText(extras.getString("recipient", "#error"));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.update, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                if (!hasAppropriateLength) {
                    idea.setError(noIdeaMessage);
                } else {
                    String now = DateTime.now().toString(ISODateTimeFormat.basicDateTime());
                    ContentValues ideaValues = new ContentValues();
                    ideaValues.put(Database.IdeasTable.IDEA, StringUtils.normalizeSpace(idea.getText().toString()));
                    ideaValues.put(Database.IdeasTable.UPDATED_AT, now);
                    SQLiteDatabase wdb = database.getWritableDatabase();
                    try {
                        wdb.beginTransaction();
                        wdb.update(Database.IdeasTable.TABLE_NAME, ideaValues, Database.IdeasTable._ID + "=?", new String[]{String.valueOf(ideaId)});
                        wdb.setTransactionSuccessful();
                    } finally {
                        wdb.endTransaction();
                    }
                    getActivity().finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
