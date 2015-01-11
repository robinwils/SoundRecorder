package android.com.soundrecorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.drive.Metadata;

import java.io.File;

/**
 * Created by rwils on 1/3/15.
 */
public class SoundListItem {
    private final int id;
    private final File file;
    private final Metadata metadata;
    private boolean showExt;

    public SoundListItem(int id, File file, boolean showExt) {
        this.id = id;
        this.file = file;
        this.showExt = showExt;
        this.metadata = null;
    }

    public SoundListItem(int id, Metadata metadata, boolean showExt) {
        this.id = id;
        this.metadata = metadata;
        this.file = null;
        this.showExt = showExt;
    }

    public int getId() {
        return id;
    }

    public String getAbsolutePath() {
        return file != null ? file.getAbsolutePath() : null;
    }
    /**
     * Returns a string containing a concise, human-readable description of this
     * object. Subclasses are encouraged to override this method and provide an
     * implementation that takes into account the object's type and data. The
     * default implementation is equivalent to the following expression:
     * <pre>
     *   getClass().getName() + '@' + Integer.toHexString(hashCode())</pre>
     * <p>See <a href="{@docRoot}reference/java/lang/Object.html#writing_toString">Writing a useful
     * {@code toString} method</a>
     * if you intend implementing your own {@code toString} method.
     *
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        String name = file == null ? metadata.getTitle() : file.getName();
        if (showExt || !name.contains("."))
            return name;
        else
            return name.substring(0, name.lastIndexOf("."));
    }

    public File getFile() {
        return file;
    }

    public void setShowExt(boolean showExt) {
        this.showExt = showExt;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
