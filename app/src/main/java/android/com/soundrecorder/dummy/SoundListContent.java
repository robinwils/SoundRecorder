package android.com.soundrecorder.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SoundListContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<SoundListItem> ITEMS = new ArrayList<SoundListItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, SoundListItem> ITEM_MAP = new HashMap<String, SoundListItem>();

    static {
        // Add 3 sample items.
        addItem(new SoundListItem("1", "Item 1"));
        addItem(new SoundListItem("2", "Item 2"));
        addItem(new SoundListItem("3", "Item 3"));
    }

    private static void addItem(SoundListItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class SoundListItem {
        public String id;
        public String content;

        public SoundListItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}