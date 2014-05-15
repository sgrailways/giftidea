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
import com.google.common.base.Strings;
import com.sgrailways.giftidea.db.Ideas;
import com.sgrailways.giftidea.core.domain.Idea;
import com.sgrailways.giftidea.wiring.BaseFragment;
import com.sgrailways.statham.ActionFactory;

import javax.inject.Inject;

public class EditIdeaFragment extends BaseFragment {
    @Inject Ideas ideas;
    @Inject Session session;
    @Inject ActionFactory actionFactory;
    @InjectView(R.id.idea) EditText ideaEditText;
    @InjectView(R.id.recipient) TextView recipient;
    Long ideaId;
    boolean isValid = false;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_idea, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.edit_idea_title));
        Bundle extras = getActivity().getIntent().getExtras();
        ideaId = extras.getLong("ideaId", -1L);
        Idea idea = ideas.findById(ideaId);
        ideaEditText.setText(idea.getText());
        isValid = validateIdeaEditText();
        ideaEditText.addTextChangedListener(new AfterTextChangedListener() {
            @Override public void afterTextChanged(Editable s) {
                isValid = validateIdeaEditText();
            }
        });
        recipient.setText(session.getActiveRecipientName());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.update, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                if (!isValid) {
                    ideaEditText.setError(getString(R.string.no_idea_message));
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
                return actionFactory.create(item, getActivity()).invoke();
        }
    }

    private boolean validateIdeaEditText() {
        return !Strings.nullToEmpty(ideaEditText.getText().toString()).isEmpty();
    }
}
