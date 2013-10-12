package com.sgrailways.giftidea.listeners;

import android.content.DialogInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class DialogDismissListenerTest {
    int unimportantInteger = 1;

    @Mock DialogInterface dialog;

    @Before public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test public void shouldBeAnInstanceOfDialogInterfaceOnClickListener() {
        assertThat(new DialogDismissListener(), isA(DialogInterface.OnClickListener.class));
    }

    @Test public void shouldCallDismiss() {
        DialogDismissListener listener = new DialogDismissListener();
        listener.onClick(dialog, unimportantInteger);
        verify(dialog, times(1)).dismiss();
    }
}