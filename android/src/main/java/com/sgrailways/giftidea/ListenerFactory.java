package com.sgrailways.giftidea;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import com.sgrailways.giftidea.core.domain.Recipient;
import com.sgrailways.giftidea.events.DeleteIdeaEvent;
import com.sgrailways.giftidea.events.GotItEvent;
import com.sgrailways.giftidea.events.RefreshIdeasListEvent;
import com.sgrailways.giftidea.listeners.DialogDismissListener;
import com.sgrailways.giftidea.wiring.ForActivity;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class ListenerFactory {
    private final Bus bus;
    private final Toaster toaster;
    private final Context context;
    private final DialogDismissListener dialogDismissListener;
    private final Session session;

    @Inject
    public ListenerFactory(Bus bus, Toaster toaster, @ForActivity Context context, DialogDismissListener dialogDismissListener, Session session) {
        this.bus = bus;
        this.toaster = toaster;
        this.context = context;
        this.dialogDismissListener = dialogDismissListener;
        this.session = session;
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

    public View.OnClickListener gotItListener(final Long id, final Recipient recipient, final String message) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                bus.post(new GotItEvent(id, recipient.getName()));
                toaster.show(message);
                bus.post(new RefreshIdeasListEvent());
            }
        };
    }

    public View.OnClickListener editIdeaListener(final Long id, final Recipient recipient) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, IdeaActivity.class);
                intent.putExtra("ideaId", id);
                session.setActiveRecipient(recipient);
                context.startActivity(intent);
            }
        };
    }

    public View.OnClickListener confirmDeleteListener(final Long id, final Recipient recipient, final String deletedMessage, final Activity activity) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity)
                        .setNegativeButton(R.string.cancel, dialogDismissListener)
                        .setPositiveButton(R.string.delete, deleteIdeaListener(id, deletedMessage))
                        .setTitle(R.string.confirmation)
                        .setMessage("Delete idea for " + recipient.getName() + "?");
                alert.create().show();
            }
        };
    }
}
