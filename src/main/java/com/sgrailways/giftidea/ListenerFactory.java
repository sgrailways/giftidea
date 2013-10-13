package com.sgrailways.giftidea;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sgrailways.giftidea.events.DeleteIdeaEvent;
import com.sgrailways.giftidea.events.GotItEvent;
import com.sgrailways.giftidea.events.RefreshIdeasListEvent;
import com.squareup.otto.Bus;

@Singleton
public class ListenerFactory {
    private final Bus bus;
    private final Toaster toaster;
    private final Context context;

    @Inject
    public ListenerFactory(Bus bus, Toaster toaster, Context context) {
        this.bus = bus;
        this.toaster = toaster;
        this.context = context;
    }

    public DialogInterface.OnClickListener deleteIdeaListener(final Long id, final String message) {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                bus.post(new DeleteIdeaEvent(id));
                toaster.show(message);
                bus.post(new RefreshIdeasListEvent());
            }
        };
    }

    public View.OnClickListener gotItListener(final Long id, final String recipientName, final String message) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                bus.post(new GotItEvent(id, recipientName));
                toaster.show(message);
                bus.post(new RefreshIdeasListEvent());
            }
        };
    }

    public View.OnClickListener editIdeaListener(final Long id, final String recipientName) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, IdeaActivity.class);
                intent.putExtra("ideaId", id);
                intent.putExtra("recipient", recipientName);
                context.startActivity(intent);
            }
        };
    }
}
