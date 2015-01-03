package android.com.soundrecorder;

import android.app.DialogFragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Chronometer;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class SoundRecorderActivity extends ActionBarActivity
        implements PlaybackDialog.NoticeDialogListener {
    private static final String LOG_TAG = "SoundRecorder";
    private Button sounds_button;
    private Button record_button;
    private Chronometer chrono;
    private MediaPlayer mPlayer;
    private String mFileName;
    private MediaRecorder mRecorder;

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
        setContentView(R.layout.activity_sound_recorder);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/temp.3gp";

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        PlaybackDialog d = (PlaybackDialog) dialog;
        String save_dir = Environment.getExternalStorageDirectory() + "/sr/";
        String filename = d.filename == null || d.filename.length() == 0 ?
                UUID.randomUUID().toString() : d.filename;
        File directory = new File(save_dir);
        directory.mkdir();
        try {
            String ext = MimeTypeMap.getFileExtensionFromUrl(new File(mFileName).toURI().toURL().toString());
            MyUtils.copyFile(mFileName, save_dir + filename + "." + ext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
