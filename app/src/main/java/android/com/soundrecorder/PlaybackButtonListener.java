package android.com.soundrecorder;

import android.media.MediaPlayer;
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

    public PlaybackButtonListener(Button playback_button, String filename) {
        this.playback_button = playback_button;
        this.filename = (filename == null || filename.length() == 0) ? MyUtils.getTempSoundFile()
                : filename;
    }

    public PlaybackButtonListener(Button playback_button) {
        this.playback_button = playback_button;
        this.filename = MyUtils.getTempSoundFile();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (mStartPlaying) {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(filename);
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
        else {
            mPlayer.release();
            mPlayer = null;
            playback_button.setText(R.string.button_play);
        }
        mStartPlaying = !mStartPlaying;
    }
}
