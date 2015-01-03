package android.com.soundrecorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getInt(ARG_ITEM_ID);
        mItem = SoundListContent.getInstance().items.get(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sound_detail, container, false);

        // Show the dummy content as text in a TextView.
        playback_button = (Button) rootView.findViewById(R.id.playback_button);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.sound_detail)).setText(mItem.toString());
            playback_button.setOnClickListener(new PlaybackButtonListener(playback_button, mItem.getAbsolutePath()));
        }

        return rootView;
    }
}
