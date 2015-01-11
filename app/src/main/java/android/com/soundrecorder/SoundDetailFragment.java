package android.com.soundrecorder;

import android.app.Activity;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A fragment representing a single Sound detail screen.
 * This fragment is either contained in a {@link SoundListActivity}
 * in two-pane mode (on tablets) or a {@link SoundDetailActivity}
 * on handsets.
 */
public class SoundDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    /**
     * The dummy content this fragment is presenting.
     */
    private SoundListItem mItem;
    private Button playback_button = null;
    private int id;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SoundDetailFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getInt(ARG_ITEM_ID);
        mItem = SoundListContent.getInstance().items.get(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sound_detail, container, false);

        playback_button = (Button) rootView.findViewById(R.id.playback_button);
        final Button save_drive_button = (Button) rootView.findViewById(R.id.save_drive_button);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.sound_detail)).setText(mItem.toString());
            playback_button.setOnClickListener(new PlaybackButtonListener(playback_button, mItem));

            if (SoundListContent.getInstance().sync && mItem.getFile() != null) {
                save_drive_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Drive.DriveApi.newDriveContents(SoundListContent.getInstance().soundRecorderActivity.mGoogleApiClient)
                                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                                    @Override
                                    public void onResult(DriveApi.DriveContentsResult result) {
                                        // If the operation was not successful, we cannot do anything
                                        // and must
                                        // fail.
                                        if (!result.getStatus().isSuccess()) {
                                            Log.i("SoundRecorder", "Failed to create new contents.");
                                            return;
                                        }
                                        // Otherwise, we can write our data to the new contents.
                                        Log.i("SoundRecorder", "New contents created.");
                                        // Get an output stream for the contents.
                                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                                        byte[] b = new byte[(int) mItem.getFile().length()];

                                        try {
                                            FileInputStream fileInputStream = null;
                                            fileInputStream = new FileInputStream(mItem.getFile());

                                            fileInputStream.read(b);
                                            outputStream.write(b);
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        // Create the initial metadata - MIME type and title.
                                        // Note that the user will be able to change the title later.
                                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                                .setMimeType("video/3gpp").setTitle(mItem.toString()).build();
                                        // Create an intent for the file chooser, and start it.
                                        IntentSender intentSender = Drive.DriveApi
                                                .newCreateFileActivityBuilder()
                                                .setInitialMetadata(metadataChangeSet)
                                                .setInitialDriveContents(result.getDriveContents())
                                                .build(SoundListContent.getInstance().soundRecorderActivity.mGoogleApiClient);
                                        try {
                                            SoundListContent.getInstance().soundRecorderActivity.startIntentSenderForResult(
                                                    intentSender, 2, null, 0, 0, 0);
                                        } catch (IntentSender.SendIntentException e) {
                                            Log.i("SoundRecorder", "Failed to launch file chooser.");
                                        }
                                    }
                                });
                    }
                });

            } else {
                save_drive_button.setEnabled(false);
            }
        }
        return rootView;
    }
}
