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

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sgrailways.giftidea.db.Ideas;
import com.sgrailways.giftidea.domain.Idea;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

public class EditIdeaFragment extends RoboFragment {
    @Inject Ideas ideas;
    @InjectView(R.id.idea) EditText ideaEditText;
    @InjectView(R.id.recipient) TextView recipient;
    @InjectResource(R.string.no_idea_message) String noIdeaMessage;
    @InjectResource(R.string.edit_idea_title) String editIdeaTitle;
    @InjectExtra("ideaId") Long ideaId;
    @InjectExtra("recipient") String recipientName;
    boolean isValid = false;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_idea, container, false);
    }

    @Override public void onResume() {
        super.onResume();
        getActivity().setTitle(editIdeaTitle);
        Idea idea = ideas.findById(ideaId);
        ideaEditText.setText(idea.getText());
        isValid = validateIdeaEditText();
        ideaEditText.addTextChangedListener(new AfterTextChangedListener() {
            @Override public void afterTextChanged(Editable s) {
                isValid = validateIdeaEditText();
            }
        });
        recipient.setText(recipientName);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.update, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                if (!isValid) {
                    ideaEditText.setError(noIdeaMessage);
                } else {
                    ideas.update(ideaId, ideaEditText.getText().toString());
                    getActivity().finish();
                }
                return true;
            case R.id.action_delete:
                ideas.delete(ideaId);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateIdeaEditText() {
        return !Strings.nullToEmpty(ideaEditText.getText().toString()).isEmpty();
    }
}
