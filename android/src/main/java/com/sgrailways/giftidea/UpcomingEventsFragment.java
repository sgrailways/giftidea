package com.sgrailways.giftidea;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sgrailways.giftidea.db.Holidays;
import com.sgrailways.giftidea.events.FlipCardEvent;
import com.sgrailways.giftidea.wiring.BaseFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;

import javax.inject.Inject;

public class UpcomingEventsFragment extends BaseFragment {
    @Inject Bus bus;
    @Inject Clock clock;
    @Inject Toaster toaster;
    @Inject Holidays holidays;
    @InjectView(R.id.carddemo) CardView cardView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
        ButterKnife.inject(this, view);
        cardView.setCard(new UpcomingCard(getActivity(), holidays, clock, bus));
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
        Card nextCard;
        if(e.toFront) {
            nextCard = new UpcomingCard(getActivity(), holidays, clock, bus);
        } else {
            nextCard = new UpcomingCardBack(getActivity(), holidays, e.id, bus, toaster);
        }
        cardView.replaceCard(nextCard);
    }

}
