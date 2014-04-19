package com.sgrailways.giftidea.listeners;

import android.content.DialogInterface;

import javax.inject.Inject;

public class DialogDismissListener implements DialogInterface.OnClickListener {

    @Inject
    public DialogDismissListener(){}

    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}
