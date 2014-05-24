package com.sgrailways.giftidea;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.sgrailways.giftidea.events.FlipCardEvent;
import com.sgrailways.giftidea.wiring.BaseFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;

import javax.inject.Inject;

public class UpcomingEventsFragment extends BaseFragment {
    @Inject Bus bus;
    @InjectView(R.id.carddemo) CardView cardView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
        ButterKnife.inject(this, view);
        cardView.setCard(new UpcomingCard(getActivity(), bus));
        return view;
    }

    @Override public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Subscribe public void answerFlipCard(FlipCardEvent e) {
        cardView.replaceCard(e.toFront ? new UpcomingCard(getActivity(), bus) : new UpcomingCardBack(getActivity(), bus));
    }

    class UpcomingCardBack extends Card {
        private final Bus bus;

        public UpcomingCardBack(Context context, Bus bus) {
            super(context, R.layout.card_upcoming_events_back);
            this.bus = bus;
            setOnClickListener(new OnCardClickListener() {
                @Override public void onClick(Card card, View view) {
                    flipToFront();
                }
            });
        }

        @Override public void setupInnerViewElements(ViewGroup parent, View view) {
            ButterKnife.inject(this, parent);
        }

        @OnClick(R.id.keep_countdown) public void onKeepCountdown(View view) {
            flipToFront();
        }

        @OnClick(R.id.stop_countdown) public void onStopCountdown(View view) {
            Toast.makeText(getActivity(), "stop clicked", Toast.LENGTH_SHORT).show();
            flipToFront();
        }

        private void flipToFront() {
            bus.post(new FlipCardEvent(true));
        }
    }

    class UpcomingCard extends Card {
        @InjectView(R.id.event_name) TextView eventName;
        @InjectView(R.id.days_remaining) TextView daysRemaining;
        @InjectView(R.id.plural_countdown_label) TextView pluralCountdownLabel;
        @InjectView(R.id.singular_countdown_label) TextView singularCountdownLabel;

        public UpcomingCard(Context context, final Bus bus) {
            super(context, R.layout.card_upcoming_events);
            setOnClickListener(new OnCardClickListener() {
                @Override public void onClick(Card card, View view) {
                    bus.post(new FlipCardEvent(false));
                }
            });
        }

        @Override public void setupInnerViewElements(ViewGroup parent, View view) {
            ButterKnife.inject(this, parent);

        }
    }
}
