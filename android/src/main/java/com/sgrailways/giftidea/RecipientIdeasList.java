package com.sgrailways.giftidea;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sgrailways.giftidea.core.domain.Recipient;
import com.sgrailways.giftidea.db.Database;
import com.sgrailways.giftidea.db.Ideas;
import com.sgrailways.giftidea.wiring.BaseActivity;
import com.squareup.picasso.Picasso;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class RecipientIdeasList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int IDEAS_LOADER = 112;
    public static final String PENDING_IDEA_IMAGE_URI = "PENDING_IDEA_IMAGE_URI";
    public static final int IDEA_CAMERA_REQUEST_CODE = 88;
    private CursorAdapter adapter;
    @Inject ListenerFactory listenerFactory;
    @Inject Session session;
    @Inject SharedPreferences preferences;
    @Inject Ideas ideas;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).inject(this);
        getLoaderManager().initLoader(IDEAS_LOADER, null, this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new IdeasCursorAdapter(getActivity());
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onResume() {
        super.onResume();
        getActivity().setTitle(session.getActiveRecipientName() + " " + getString(R.string.app_name));
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != IDEA_CAMERA_REQUEST_CODE) {
            Timber.d("Request not for the camera activity. Passing on to super");
            super.onActivityResult(requestCode, resultCode, data);
        }
        SharedPreferences.Editor editor = preferences.edit();
        String imageUrl = preferences.getString(PENDING_IDEA_IMAGE_URI, "");
        if ("".equals(imageUrl)) {
            return;
        }
        switch (resultCode) {
            case Activity.RESULT_OK:
                Timber.d("Camera activity finished successfully");
                Long ideaId = Long.valueOf(imageUrl.split("-")[1]);
                ideas.updateImageUrl(ideaId, imageUrl);
                break;
            case Activity.RESULT_CANCELED:
                Timber.d("Camera activity was cancelled");
                if (new File(imageUrl).delete()) {
                    Timber.d("File at '%s' created for idea image capture was deleted upon cancel", imageUrl);
                } else {
                    Timber.e("Failed to delete file at '%s' created for idea image", imageUrl);
                }
                break;
            default:
                Timber.e("Unexpected result code from idea photo request");
                break;
        }
        editor.remove(PENDING_IDEA_IMAGE_URI);
        editor.apply();
        Timber.d("Pending image url removed from session");
    }

    @Override public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case IDEAS_LOADER:
                return new CursorLoader(getActivity(), Ideas.URI, Ideas.COLUMNS, Ideas.QUERY_BY_RECIPIENT_ID, new String[]{session.getActiveRecipientId()}, Ideas.DEFAULT_SORT);
            default:
                return null;
        }
    }

    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    public class IdeasCursorAdapter extends CursorAdapter {
        private final LayoutInflater inflater;

        public IdeasCursorAdapter(Context context) {
            super(context, null, 0);
            inflater = LayoutInflater.from(context);
        }

        @Override public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            ViewHolder holder = new ViewHolder();
            View view = inflater.inflate(R.layout.idea_item, viewGroup, false);
            holder.image = (ImageView) view.findViewById(R.id.idea_image);
            holder.idea = (TextView) view.findViewById(R.id.idea);
            holder.gotIt = (TextView) view.findViewById(R.id.got_it);
            view.setTag(holder);
            return view;
        }

        @Override public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.idea.setText(cursor.getString(Ideas.COLUMN_INDEXES.get(Database.IdeasTable.IDEA)));
            Recipient recipient = session.getActiveRecipient();
            final long id = cursor.getLong(Ideas.COLUMN_INDEXES.get(Database.IdeasTable._ID));
            view.setId((int) id);
            final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            boolean cameraIsAvailable = takePictureIntent.resolveActivity(context.getPackageManager()) != null;
            holder.image.setVisibility(cameraIsAvailable ? View.VISIBLE : View.GONE);
            if (cameraIsAvailable) {
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile(id);
                            Timber.d("Created filename '%s'", photoFile.getAbsolutePath());
                        } catch (IOException e) {
                            Timber.e(e, "Failed to create file");
                        }
                        if (photoFile != null) {
                            SharedPreferences.Editor editor = preferences.edit();
                            Uri uri = Uri.fromFile(photoFile);
                            editor.putString(PENDING_IDEA_IMAGE_URI, uri.toString());
                            editor.apply();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(takePictureIntent, IDEA_CAMERA_REQUEST_CODE);
                        }
                    }
                });
                String ideaImageUri = cursor.getString(Ideas.COLUMN_INDEXES.get(Database.IdeasTable.IMAGE_URI));
                Timber.d("Found image uri '%s'", ideaImageUri);
                if (TextUtils.isEmpty(ideaImageUri)) {
                    Picasso.with(context).load(android.R.drawable.ic_menu_camera).into(holder.image);
                } else {
                    Picasso.with(context).load(ideaImageUri).resize(64, 64).centerCrop().into(holder.image);
                }
            }
            boolean done = Boolean.parseBoolean(cursor.getString(Ideas.COLUMN_INDEXES.get(Database.IdeasTable.IS_DONE)));
            holder.gotIt.setVisibility(done ? View.GONE : View.VISIBLE);
            if (done) {
                holder.idea.setPaintFlags(holder.idea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.idea.setOnClickListener(listenerFactory.confirmDeleteListener(id, recipient, getString(R.string.finished_idea_deleted_message), getActivity()));
            } else {
                holder.gotIt.setOnClickListener(listenerFactory.gotItListener(id, recipient, getString(R.string.got_it_message)));
                holder.idea.setOnClickListener(listenerFactory.editIdeaListener(id, recipient));
                holder.idea.setPaintFlags(holder.idea.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        private File createImageFile(long ideaId) throws IOException {
            return File.createTempFile("idea-" + ideaId + "-", ".jpg", getActivity().getExternalFilesDir(null));
        }
    }

    static class ViewHolder {
        ImageView image;
        TextView idea;
        TextView gotIt;
    }
}
