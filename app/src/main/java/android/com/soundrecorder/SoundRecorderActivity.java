package android.com.soundrecorder;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Chronometer;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataBuffer;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class SoundRecorderActivity extends ActionBarActivity
        implements PlaybackDialog.NoticeDialogListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String LOG_TAG = "SoundRecorder";
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    private Button sounds_button;
    private Button record_button;
    private Chronometer chrono;
    private MediaPlayer mPlayer;
    private String mFileName;
    private MediaRecorder mRecorder;
    protected GoogleApiClient mGoogleApiClient;
    protected MetadataBuffer metadataBuffer;
    private String mNextPageToken;

    private final ResultCallback<DriveApi.MetadataBufferResult> metadataBufferCallback = new ResultCallback<DriveApi.MetadataBufferResult>() {
        @Override
        public void onResult(DriveApi.MetadataBufferResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.d(LOG_TAG, "PROBLEM ON RESULT");
                return;
            }
            Log.d(LOG_TAG, new Integer(result.getMetadataBuffer().getCount()).toString());
            metadataBuffer = result.getMetadataBuffer();
            SoundListContent.getInstance().items.clear();
        }
    };

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorder.start();
        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.start();
        record_button.setText(R.string.button_stop);
    }

    private void stopRecording() {
        mRecorder.stop();
        chrono.stop();
        record_button.setText(R.string.button_record);
        mRecorder.release();
        mRecorder = null;
        DialogFragment dialog = new PlaybackDialog();
        dialog.show(getFragmentManager(), "playback");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "ALLOOOO");
        setContentView(R.layout.activity_sound_recorder);
        if (SoundListContent.getInstance().soundRecorderActivity == null)
            SoundListContent.getInstance().setSoundRecorderActivity(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean("sync", false))
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();


        new File(MyUtils.getSRDir()).mkdirs();
        mFileName = MyUtils.getTempSoundFile();

        sounds_button = (Button) findViewById(R.id.sounds_button);
        sounds_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent soundListActivity = new Intent(getApplicationContext(), SoundListActivity.class);
                startActivity(soundListActivity);
            }
        });

        chrono = (Chronometer) findViewById(R.id.chronometer);

        record_button = (Button) findViewById(R.id.record_button);
        record_button.setOnClickListener(new View.OnClickListener() {
            boolean mStartRecording = true;

            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sound_recorder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivityForResult(settings, 42);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        PlaybackDialog d = (PlaybackDialog) dialog;
        String save_dir = MyUtils.getSRDir();
        String filename = d.filename == null || d.filename.length() == 0 ?
                UUID.randomUUID().toString() : d.filename;
        try {
            String ext = MimeTypeMap.getFileExtensionFromUrl(new File(mFileName).toURI().toURL().toString());
            MyUtils.copyFile(mFileName, save_dir + filename + "." + ext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDriveService() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("sync", false) && mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        DriveFolder driveFolder = Drive.DriveApi.getRootFolder(mGoogleApiClient);

        driveFolder.listChildren(mGoogleApiClient).setResultCallback(metadataBufferCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
            case 42:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (sharedPreferences.getBoolean("sync", false))
                    startDriveService();
                SoundListContent.getInstance().showExt = sharedPreferences.getBoolean("ext", true);
                SoundListContent.getInstance().sync = sharedPreferences.getBoolean("sync", false);
                break;
        }

    }
}
