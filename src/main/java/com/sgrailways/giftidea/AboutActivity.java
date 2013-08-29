package com.sgrailways.giftidea;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class AboutActivity extends RoboActivity {
    @InjectView(R.id.contact_blog) TextView blog;
    @InjectView(R.id.contact_twitter) TextView twitter;
    @InjectView(R.id.contact_email) TextView email;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.about_title);
    }

    @Override protected void onResume() {
        blog.setMovementMethod(LinkMovementMethod.getInstance());
        twitter.setMovementMethod(LinkMovementMethod.getInstance());
        email.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("mailto:sghill@sgrailways.com?subject=" + Uri.encode("GiftIdea! Feedback")));
                startActivity(Intent.createChooser(sendIntent, "Send email"));
            }
        });
        super.onResume();
    }
}
