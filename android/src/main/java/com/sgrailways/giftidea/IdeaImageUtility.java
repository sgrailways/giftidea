package com.sgrailways.giftidea;

import android.content.SharedPreferences;
import android.net.Uri;
import timber.log.Timber;

import java.io.File;

public final class IdeaImageUtility {
    private IdeaImageUtility() {
    }

    public static boolean destroyPendingIdeaImage(SharedPreferences preferences) {
        String imageUri = preferences.getString(PreferenceKeys.PENDING_IDEA_IMAGE_URI, "");
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PreferenceKeys.PENDING_IDEA_IMAGE_URI);
        editor.apply();
        if ("".equals(imageUri)) {
            Timber.d("No image uri found");
            return true;
        }
        Uri parsed = Uri.parse(imageUri);
        File file = new File(parsed.getPath());
        if (!file.exists()) {
            Timber.d("No file found at specified image path '%s'", file.getAbsolutePath());
            return true;
        }
        if (file.delete()) {
            Timber.d("Successfully deleted image at '%s'", file.getAbsolutePath());
        } else {
            Timber.e("Failed to delete image at '%s'", file.getAbsolutePath());
        }
        return file.delete();
    }
}
