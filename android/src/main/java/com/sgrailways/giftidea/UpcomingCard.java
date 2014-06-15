package com.sgrailways.giftidea;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sgrailways.giftidea.core.domain.Holiday;
import com.sgrailways.giftidea.db.Holidays;
import com.sgrailways.giftidea.events.FlipCardEvent;
import com.squareup.otto.Bus;
import it.gmariotti.cardslib.library.internal.Card;

public class UpcomingCard extends Card {
    private final Bus bus;
    private final Clock clock;
    private final Holidays holidays;
    @InjectView(R.id.event_name) TextView eventName;
    @InjectView(R.id.days_remaining) TextView daysRemaining;
    @InjectView(R.id.plural_countdown_label) TextView pluralCountdownLabel;
    @InjectView(R.id.singular_countdown_label) TextView singularCountdownLabel;
    @InjectView(R.id.today_label) TextView todayLabel;
    @InjectView(R.id.future_label) LinearLayout futureLabel;

    public UpcomingCard(Context context, Holidays holidays, Clock clock, Bus bus) {
        super(context, R.layout.card_upcoming_events);
        this.bus = bus;
        this.clock = clock;
        this.holidays = holidays;
    }

    @Override public void setupInnerViewElements(ViewGroup parent, View view) {
        ButterKnife.inject(this, parent);
        final Holiday holiday = holidays.findNext();
        int daysUntil = clock.daysUntil(holiday.getCelebratedAtLocalDate());
        eventName.setText(holiday.getName());
        if (daysUntil == 0) {
            todayLabel.setVisibility(View.VISIBLE);
            futureLabel.setVisibility(View.GONE);
        } else {
            todayLabel.setVisibility(View.GONE);
            futureLabel.setVisibility(View.VISIBLE);
            daysRemaining.setText(String.valueOf(daysUntil));
            pluralCountdownLabel.setVisibility(daysUntil == 1 ? View.GONE : View.VISIBLE);
            singularCountdownLabel.setVisibility(daysUntil == 1 ? View.VISIBLE : View.GONE);
        }
        setOnClickListener(new OnCardClickListener() {
            @Override public void onClick(Card card, View view) {
                bus.post(new FlipCardEvent(holiday.getId()));
            }
        });
    }
}
