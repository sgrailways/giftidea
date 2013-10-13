package com.sgrailways.giftidea;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sgrailways.giftidea.events.DeleteIdeaEvent;
import com.sgrailways.giftidea.events.GotItEvent;
import com.sgrailways.giftidea.events.RefreshIdeasListEvent;
import com.sgrailways.giftidea.listeners.DialogDismissListener;
import com.squareup.otto.Bus;

@Singleton
public class ListenerFactory {
    private final Bus bus;
    private final Toaster toaster;
    private final Context context;
    private final DialogDismissListener dialogDismissListener;

    @Inject
    public ListenerFactory(Bus bus, Toaster toaster, Context context, DialogDismissListener dialogDismissListener) {
        this.bus = bus;
        this.toaster = toaster;
        this.context = context;
        this.dialogDismissListener = dialogDismissListener;
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

    public View.OnClickListener confirmDeleteListener(final Long id, final String recipientName, final String deletedMessage) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context)
                        .setNegativeButton(R.string.cancel, dialogDismissListener)
                        .setPositiveButton(R.string.delete, deleteIdeaListener(id, deletedMessage))
                        .setTitle(R.string.confirmation)
                        .setMessage("Delete idea for " + recipientName + "?");
                alert.create().show();
            }
        };
    }
}
