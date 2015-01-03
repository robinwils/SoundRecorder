package android.com.soundrecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rwils on 1/3/15.
 */
public class SoundListContent {
    private static SoundListContent ourInstance = new SoundListContent();

    public static SoundListContent getInstance() {
        return ourInstance;
    }

    public final List<SoundListItem> items = new ArrayList<SoundListItem>();

    private SoundListContent() {
        File[] sound_files = MyUtils.listSoundsDirectory();
        for (int i = 0; i < sound_files.length; ++i) {
            items.add(new SoundListItem(i, sound_files[i]));
        }
    }
}
