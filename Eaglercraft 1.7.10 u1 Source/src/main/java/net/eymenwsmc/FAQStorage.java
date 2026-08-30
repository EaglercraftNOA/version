package net.eymenwsmc;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;

public class FAQStorage {
    private static final String PATH = "eymen/faq.json";

    public static List<FAQItem> load() {
        List<FAQItem> list = new ArrayList<FAQItem>();
        VFile2 f = new VFile2(PATH);
        if (!f.exists()) {
            return list;
        }
        try {
            String json = f.getAllChars();
            if (json == null || json.isEmpty()) return list;
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String q = o.optString("q", "");
                String a = o.optString("a", "");
                list.add(new FAQItem(q, a));
            }
        } catch (Throwable t) {
            // ignore and return empty
        }
        return list;
    }

    public static void save(List<FAQItem> items) {
        JSONArray arr = new JSONArray();
        for (FAQItem it : items) {
            JSONObject o = new JSONObject();
            o.put("q", it.question == null ? "" : it.question);
            o.put("a", it.answer == null ? "" : it.answer);
            arr.put(o);
        }
        VFile2 f = new VFile2(PATH);
        f.setAllChars(arr.toString());
    }
}
