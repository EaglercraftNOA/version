package net.eymenwsmc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FAQData {
    private static final List<FAQItem> ITEMS;

    static {
        List<FAQItem> tmp = new ArrayList<FAQItem>();
        add(tmp, "Why do NPC skins and holograms look strange when I join with Minecraft 1.7.10 on a ViaVersion (1.8+) server?", "Because Minecraft 1.7 does not support many of the features introduced in 1.8 (like armor stands, skin layers, and hologram text rendering), connecting with 1.7.10 causes NPC skins to break and holograms to display incorrectly. To see them properly, you need to join with a 1.8+ client.");
        add(tmp, "How do I join a 1.12/1.8 server in 1.7.10?", "Normally you can’t, because 1.7.10 doesn’t understand the newer protocols. However, you can use the EaglerXRewind plugin, which adds backward compatibility and lets 1.7.10 clients connect to 1.8–1.12 servers. Server owners just need to install the plugin alongside ViaVersion/ProtocolSupport.");
        add(tmp, "Performance tips?", "Lower render distance and turn off fancy graphics in Options.");
        add(tmp, "Can I make my own 1.7.10 server?", "Yes, you can use EaglerXRewind plugin in your 1.8/1.12 servers. But If you want the backend to be 1.7.10, then you need to make a velocity server with EaglerXServer and EaglerXRewind plugin. Then make the normal 1.7.10 server and add it to velocity..");
        add(tmp, "Why am I getting a bad FPS?", "Because this client is not that optimized as 1.8, but I will be backporting optimizitations from 1.8");
        add(tmp, "Why did you port 1.7.10?", "Because I saw people wanting 1.7.10 to be ported, I think they love the combat of 1.7.10");
        add(tmp, "Why does some plugins does not work?", "Many plugins are built for newer Minecraft versions (1.8+), which include features 1.7.10 doesn’t support—such as armor stands, JSON chat, advanced holograms, or extra skin layers. When you connect with 1.7.10, these elements either don’t render at all or look broken because your client simply cannot display them. To avoid issues, use a 1.8+ client or ask the server owner if they support 1.7 with compatibility plugins (like ProtocolSupport/EaglerXRewind).");
        add(tmp, "Why does TAB List looks weird?", "Well, because if you try booting vanilla 1.7, it will look much worser than 1.7 eagler. I tried to fix it but thats all I could do.");
        ITEMS = Collections.unmodifiableList(tmp);
    }

    private static void add(List<FAQItem> list, String q, String a) {
        list.add(new FAQItem(q, a));
    }

    public static List<FAQItem> items() {
        return ITEMS;
    }
}
