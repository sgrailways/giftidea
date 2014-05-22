package com.sgrailways.giftidea;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.sgrailways.giftidea.wiring.BaseFragment;

public class UpcomingEventsFragment extends BaseFragment {
    @InjectView(R.id.event_name) TextView eventName;
    @InjectView(R.id.days_remaining) TextView daysRemaining;
    @InjectView(R.id.plural_countdown_label) TextView pluralCountdownLabel;
    @InjectView(R.id.singular_countdown_label) TextView singularCountdownLabel;
    @InjectView(R.id.countdown_leader_label) TextView countdownLeaderLabel;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.no_celebration) public void launchNoCelebration() {
        Toast.makeText(getActivity(), "no celebration", Toast.LENGTH_SHORT).show();
    }
}
