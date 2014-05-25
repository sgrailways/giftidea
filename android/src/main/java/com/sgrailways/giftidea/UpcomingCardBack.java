package com.sgrailways.giftidea;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.sgrailways.giftidea.core.domain.Holiday;
import com.sgrailways.giftidea.db.Holidays;
import com.sgrailways.giftidea.events.FlipCardEvent;
import com.squareup.otto.Bus;
import it.gmariotti.cardslib.library.internal.Card;
import timber.log.Timber;

public class UpcomingCardBack extends Card {
    private Context context;
    private final long id;
    private final Bus bus;
    private final Holidays holidays;
    private final Toaster toaster;
    @InjectView(R.id.countdown_configuration_holiday_label) TextView eventName;

    public UpcomingCardBack(Context context, Holidays holidays, long id, Bus bus, Toaster toaster) {
        super(context, R.layout.card_upcoming_events_back);
        this.context = context;
        this.id = id;
        this.bus = bus;
        this.holidays = holidays;
        this.toaster = toaster;
        setOnClickListener(new OnCardClickListener() {
            @Override public void onClick(Card card, View view) {
                flipToFront();
            }
        });
    }

    @Override public void setupInnerViewElements(ViewGroup parent, View view) {
        ButterKnife.inject(this, parent);
        Holiday holiday = holidays.findById(id);
        eventName.setText(holiday.getName());
    }

    @OnClick(R.id.keep_countdown) public void onKeepCountdown(View view) {
        flipToFront();
    }

    @OnClick(R.id.stop_countdown) public void onStopCountdown(View view) {
        if (holidays.stopCountdown(id)) {
            toaster.show(context.getString(R.string.stop_countdown_message));
        } else {
            Timber.e("Could not stop countdown for holiday " + id);
        }
        flipToFront();
    }

    private void flipToFront() {
        bus.post(new FlipCardEvent());
    }
}
