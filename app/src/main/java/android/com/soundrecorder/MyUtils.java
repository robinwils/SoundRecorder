package android.com.soundrecorder;

import android.os.Environment;
import android.os.PatternMatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by rwils on 1/3/15.
 */
public class MyUtils {
    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static void copyFile(String src, String dst) throws IOException {
        copyFile(new File(src), new File(dst));
    }

    public static String getTempSoundFile()
    {
        return Environment.getExternalStorageDirectory() + "/temp.3gp";
    }

    public static String getSRDir()
    {
        return Environment.getExternalStorageDirectory() + "/sr/";
    }

    public static File[] listSoundsDirectory()
    {
        File f = new File(getSRDir());
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                // is directory
            }
        }
        return files;
    }
}
