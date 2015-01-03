package android.com.soundrecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.com.soundrecorder.R;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

/**
 * Created by rwils on 1/3/15.
 */
public class PlaybackDialog extends DialogFragment {
    public String filename;
    private boolean mStartPlaying = true;
    private MediaPlayer mPlayer;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.playback_dialog_layout, null);
        final EditText filename_text = (EditText) view.findViewById(R.id.filename_text);
        builder.setView(view)
                .setMessage(R.string.dialog_keep_sound)
                .setPositiveButton(R.string.button_keep, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filename = filename_text.getText().toString();
                        mListener.onDialogPositiveClick(PlaybackDialog.this);
                    }
                })
                .setNegativeButton(R.string.button_discard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(PlaybackDialog.this);
                    }
                });

        Button playback_button = (Button) view.findViewById(R.id.playback_button);
        playback_button.setOnClickListener(new PlaybackButtonListener(playback_button));
        return builder.create();
    }
}
