package com.cnet236.asta.dummy;

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
public class TestContent {
    public static String authHost = "192.168.43.222";
    /**
     * An array of sample (dummy) items.
     */
    public static List<TestResult> ITEMS = new ArrayList<TestResult>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, TestResult> ITEM_MAP = new HashMap<String, TestResult>();

    /*static {
        // Add 3 sample items.
        addItem(new TestResult("1", "Item 1", 0));
        addItem(new TestResult("2", "Item 2", 1));
        addItem(new TestResult("3", "Item 3", 1));
    }*/

    public static void addItem(TestResult item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.testName, item);
    }

    public static void clearList() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class TestResult {
        public String testName;
        public String content;
        public int colour;

        public TestResult(String name, String content, int colour) {
            this.testName = name;
            this.content = content;
            this.colour = colour;
        }

        @Override
        public String toString() {
            return testName;
        }
    }
}
