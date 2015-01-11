package android.com.soundrecorder;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

/**
 * Created by rwils on 1/3/15.
 */
public class PlaybackButtonListener implements View.OnClickListener{
    private boolean mStartPlaying = true;
    private MediaPlayer mPlayer;
    private Button playback_button;
    private String filename;
    private SoundListItem item;

    public PlaybackButtonListener(Button playback_button, SoundListItem item) {
        this.playback_button = playback_button;
        this.filename = item.getAbsolutePath();
        this.item = item;
    }

    public PlaybackButtonListener(Button playback_button) {
        this.playback_button = playback_button;
        this.filename = MyUtils.getTempSoundFile();
        this.item = null;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (mStartPlaying)
            playItem();
        else
            stopItem();

        mStartPlaying = !mStartPlaying;
    }

    private void stopItem() {
        if (this.filename == null) {

        }
        else {
            mPlayer.release();
            mPlayer = null;
            playback_button.setText(R.string.button_play);
        }
    }

    private void playItem() {
        String dataSource = this.filename == null ? this.item.getMetadata().getWebContentLink()
                : this.filename;


            mPlayer = new MediaPlayer();

            try {
                mPlayer.setDataSource(dataSource);
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.prepare();
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playback_button.setText(R.string.button_play);
                    }
                });
                mPlayer.start();
                playback_button.setText(R.string.button_stop);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
