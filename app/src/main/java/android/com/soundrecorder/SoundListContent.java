package android.com.soundrecorder;

import android.util.Log;

import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by rwils on 1/3/15.
 */
public class SoundListContent {
    private static SoundListContent ourInstance = new SoundListContent();

    public static SoundListContent getInstance() {
        return ourInstance;
    }

    public final List<SoundListItem> items = new ArrayList<SoundListItem>();
    public SoundRecorderActivity soundRecorderActivity = null;
    public boolean showExt = true;
    public boolean sync = false;

    private SoundListContent() {
    }

    public void buildItems() {
        File[] sound_files = MyUtils.listSoundsDirectory();
        int i;

        MetadataBuffer metadataBuffer = SoundListContent.getInstance().soundRecorderActivity.metadataBuffer;

        for (i = 0; i < sound_files.length; ++i) {
            items.add(new SoundListItem(i, sound_files[i], true));
        }

        if (metadataBuffer != null)
            for (Metadata metadata : metadataBuffer) {
                boolean add = true;
                for (File f : sound_files) {
                    if (f.getName() == metadata.getTitle()) {
                        add = false;
                        break;
                    }
                }
                Log.d("SoundRecorder", metadata.getMimeType());
                if (add && metadata.getMimeType().equals("video/3gpp"))
                    items.add(new SoundListItem(i, metadata, true));
            }

    }

    public void setSoundRecorderActivity(SoundRecorderActivity activity) {
        this.soundRecorderActivity = activity;
    }

    public void setShowExt() {
        if (items.isEmpty())
            buildItems();
        for (SoundListItem item : items) {
            item.setShowExt(this.showExt);
        }
    }
}
