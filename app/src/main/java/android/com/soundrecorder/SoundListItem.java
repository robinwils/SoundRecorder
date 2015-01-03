package android.com.soundrecorder;

import java.io.File;

/**
 * Created by rwils on 1/3/15.
 */
public class SoundListItem {
    private final int id;
    private final File file;

    public SoundListItem(int id, File file) {
        this.id = id;
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
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
        return file.getName();
    }
}
