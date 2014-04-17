package com.sgrailways.giftidea;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.sgrailways.giftidea.core.HashTagLocator;
import com.sgrailways.giftidea.db.Ideas;
import com.sgrailways.statham.ActionFactory;
import roboguice.fragment.RoboFragment;

import java.util.LinkedHashSet;

public class NewIdeaFragment extends RoboFragment {
    @Inject Ideas ideas;
    @Inject ActionFactory actionFactory;
    @Inject HashTagLocator hashTagLocator;
    @InjectView(R.id.idea) EditText idea;
    @InjectView(R.id.recipients_view) TextView recipients;
    @InjectView(R.id.hash_tag_count) TextView hashTagCount;
    @InjectView(R.id.gift_ideas_label) TextView giftIdeasLabel;
    Joiner joiner = Joiner.on(", ");
    boolean hasHashTags = false;
    boolean hasAppropriateLength = false;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_idea, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override public void onResume() {
        getActivity().setTitle(getString(R.string.new_idea_title));
        idea.addTextChangedListener(new AfterTextChangedListener() {
            public void afterTextChanged(Editable s) {
                LinkedHashSet<String> hashTags = hashTagLocator.findAllIn(s.toString());
                hashTagCount.setText(String.valueOf(hashTags.size()));
                giftIdeasLabel.setText(hashTags.size() == 1 ? getString(R.string.gift_idea) : getString(R.string.gift_ideas));
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
                    idea.setError(getString(R.string.no_hash_tags_message));
                } else if (!hasAppropriateLength) {
                    idea.setError(getString(R.string.no_idea_message));
                } else {
                    ideas.createFromText(idea.getText().toString());
                    getActivity().finish();
                }
                return true;
            default:
                return actionFactory.create(item, getActivity()).invoke();
        }
    }
}
