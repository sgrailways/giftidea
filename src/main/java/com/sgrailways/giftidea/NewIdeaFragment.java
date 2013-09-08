package com.sgrailways.giftidea;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.sgrailways.giftidea.db.Database;
import com.sgrailways.giftidea.db.Recipients;
import com.sgrailways.giftidea.domain.MissingRecipient;
import com.sgrailways.giftidea.domain.Recipient;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import java.util.LinkedHashSet;

public class NewIdeaFragment extends RoboFragment {
    @Inject Database database;
    @Inject Recipients recipientsRepository;
    @Inject HashTagLocator hashTagLocator;
    @InjectView(R.id.idea) EditText idea;
    @InjectView(R.id.recipients_view) TextView recipients;
    @InjectView(R.id.hash_tag_count) TextView hashTagCount;
    @InjectView(R.id.gift_ideas_label) TextView giftIdeasLabel;
    @InjectResource(R.string.gift_idea) String giftIdea;
    @InjectResource(R.string.gift_ideas) String giftIdeas;
    @InjectResource(R.string.no_hash_tags_message) String noHashTagsMessage;
    @InjectResource(R.string.no_idea_message) String noIdeaMessage;
    @InjectResource(R.string.new_idea_title) String newIdeaTitle;
    Joiner joiner = Joiner.on(", ");
    boolean hasHashTags = false;
    boolean hasAppropriateLength = false;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_idea, container, false);
    }

    @Override public void onResume() {
        getActivity().setTitle(newIdeaTitle);
        idea.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                LinkedHashSet<String> hashTags = hashTagLocator.findAllIn(s.toString());
                hashTagCount.setText(String.valueOf(hashTags.size()));
                giftIdeasLabel.setText(hashTags.size() == 1 ? giftIdea : giftIdeas);
                hasHashTags = hashTags.size() > 0;
                hasAppropriateLength = hashTagLocator.removeAllFrom(s.toString()).length() > 0;
                recipients.setText(joiner.join(hashTags));
            }
        });
        super.onResume();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.save, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                if (!hasHashTags) {
                    idea.setError(noHashTagsMessage);
                } else if (!hasAppropriateLength) {
                    idea.setError(noIdeaMessage);
                } else {
                    String now = DateTime.now().toString(ISODateTimeFormat.basicDateTime());
                    ContentValues ideaValues = new ContentValues();
                    ideaValues.put(Database.IdeasTable.IDEA, hashTagLocator.removeAllFrom(idea.getText().toString()));
                    ideaValues.put(Database.IdeasTable.IS_DONE, String.valueOf(false));
                    ideaValues.put(Database.IdeasTable.CREATED_AT, now);
                    ideaValues.put(Database.IdeasTable.UPDATED_AT, now);
                    SQLiteDatabase wdb = database.getWritableDatabase();
                    LinkedHashSet<String> hashTags = hashTagLocator.findAllIn(idea.getText().toString());
                    try {
                        wdb.beginTransaction();
                        for (String hashTag : hashTags) {
                            Recipient recipient = recipientsRepository.findByName(hashTag);
                            long recipientId;
                            if (recipient instanceof MissingRecipient) {
                                recipientId = recipientsRepository.createFromName(hashTag).getId();
                            } else {
                                recipientId = recipient.getId();
                                recipientsRepository.incrementIdeaCountFor(recipient);
                            }
                            ideaValues.put(Database.IdeasTable.RECIPIENT_ID, recipientId);
                            wdb.insert(Database.IdeasTable.TABLE_NAME, null, ideaValues);
                        }
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
