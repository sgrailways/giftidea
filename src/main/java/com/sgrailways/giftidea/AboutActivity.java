package com.sgrailways.giftidea;

import android.os.Bundle;
import roboguice.activity.RoboActivity;

public class AboutActivity extends RoboActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.about_title);
    }
}
