package com.sgrailways.giftidea.listeners;

import android.content.DialogInterface;

public class DialogDismissListener implements DialogInterface.OnClickListener {
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}
