package com.sgrailways.giftidea;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.sgrailways.HashTagLocator;
import com.sgrailways.R;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.util.LinkedHashSet;

public class NewIdeaFragment extends RoboFragment {
    @Inject HashTagLocator hashTagLocator;
    @InjectView(R.id.idea) EditText idea;
    @InjectView(R.id.recipients_view) TextView recipients;
    Joiner joiner = Joiner.on(", ");

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_idea, container, false);
    }

    @Override public void onResume() {
        super.onResume();
        idea.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                LinkedHashSet<String> hashTags = hashTagLocator.findAllIn(s.toString());
                recipients.setText(joiner.join(hashTags));
            }
        });
    }
}
