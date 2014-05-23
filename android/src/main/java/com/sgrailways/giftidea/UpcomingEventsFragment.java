package com.sgrailways.giftidea;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sgrailways.giftidea.wiring.BaseFragment;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;

public class UpcomingEventsFragment extends BaseFragment {
    @InjectView(R.id.carddemo) CardView cardView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
        ButterKnife.inject(this, view);
        cardView.setCard(new UpcomingCard(getActivity()));
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    class UpcomingCard extends Card {
        private final static int FRONT = R.layout.card_upcoming_events;
        private final static int BACK = R.layout.card_upcoming_events_back;

        public UpcomingCard(Context context) {
            this(context, FRONT);
        }

        public UpcomingCard(Context context, int innerLayout) {
            super(context, innerLayout);
            init();
        }

        private void init() {
            setOnClickListener(new OnCardClickListener() {
                @Override public void onClick(Card card, View view) {
                    boolean isOnFront = UpcomingCard.this.getInnerLayout() == FRONT;
                    UpcomingCard.this.setInnerLayout(isOnFront ? BACK : FRONT);
                    cardView.replaceCard(UpcomingCard.this);
                }
            });
        }

        @Override public void setupInnerViewElements(ViewGroup parent, View view) {
            View keepCountdown = parent.findViewById(R.id.keep_countdown);
            View stopCountdown = parent.findViewById(R.id.stop_countdown);

            if(keepCountdown != null) {
                keepCountdown.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        Toast.makeText(getActivity(), "continue clicked", Toast.LENGTH_SHORT).show();
                        cardView.callOnClick();
                    }
                });
            }
            if(stopCountdown != null) {
                stopCountdown.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        Toast.makeText(getActivity(), "stop clicked", Toast.LENGTH_SHORT).show();
                        cardView.callOnClick();
                    }
                });
            }
        }
    }
}
