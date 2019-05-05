package com.product.sampling.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();
    public static final List<DummyItem> TASK_ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 3;

    static {
        // Add some sample items.
        for (int i = 0; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
        for (int i = 0; i <= 2; i++) {
            addTaskItem(createTaskItem(i));
        }

    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static void addTaskItem(DummyItem item) {
        TASK_ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        String text = "";
        switch (position) {
            case 0:
                text = "待办任务";
                break;
            case 1:
                text = "未上传";
                break;
            case 2:
                text = "已上传";
                break;
            case 3:
                text = "我的信息";
                break;
        }
        return new DummyItem(String.valueOf(position), text, makeDetails(position));
    }

    private static DummyItem createTaskItem(int position) {
        String text = "";
        switch (position) {
            case 0:
                text = "任务信息";
                break;
            case 1:
                text = "现场信息";
                break;
            case 2:
                text = "样品信息";
                break;
        }
        return new DummyItem(String.valueOf(position), text, makeDetails(position));
    }


    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
