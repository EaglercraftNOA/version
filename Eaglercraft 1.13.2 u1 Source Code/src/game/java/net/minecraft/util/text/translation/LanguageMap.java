package net.minecraft.util.text.translation;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageMap {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern NUMERIC_VARIABLE_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   private static final LanguageMap INSTANCE = new LanguageMap();
   private final Map<String, String> languageList = Maps.newHashMap();
   private long lastUpdateTimeInMilliseconds;

   public LanguageMap() {
      try {
         InputStream inputstream = EagRuntime.getResourceStream("/assets/minecraft/lang/en_us.json");
         if (inputstream == null) {
            inputstream = LanguageMap.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
         }
         if (inputstream != null) {
            JsonElement jsonelement = (new Gson()).fromJson(new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject jsonobject = JsonUtils.getJsonObject(jsonelement, "strings");

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               String s = NUMERIC_VARIABLE_PATTERN.matcher(JsonUtils.getString(entry.getValue(), entry.getKey())).replaceAll("%s");
               this.languageList.put(entry.getKey(), s);
            }

            this.lastUpdateTimeInMilliseconds = Util.milliTime();
         } else {
            LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json");
         }
      } catch (JsonParseException jsonparseexception) {
         LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)jsonparseexception);
      }

   }

   public static LanguageMap getInstance() {
      return INSTANCE;
   }

   @OnlyIn(Dist.CLIENT)
   public static synchronized void replaceWith(Map<String, String> p_135063_0_) {
      INSTANCE.languageList.clear();
      INSTANCE.languageList.putAll(p_135063_0_);
      INSTANCE.lastUpdateTimeInMilliseconds = Util.milliTime();
      SingleplayerServerController.updateLocale(dump());
   }

   public synchronized String translateKey(String key) {
      return this.tryTranslateKey(key);
   }

   private String tryTranslateKey(String key) {
      String s = this.languageList.get(key);
      if (s != null) {
         return s;
      }

      s = this.languageList.get("eaglercraft." + key);
      if (s != null) {
         return s;
      }

      if (key.startsWith("eaglercraft.")) {
         s = this.languageList.get(key.substring(12));
         if (s != null) {
            return s;
         }
      }

      return key;
   }

   public synchronized boolean exists(String p_210813_1_) {
      return this.languageList.containsKey(p_210813_1_) || this.languageList.containsKey("eaglercraft." + p_210813_1_) || p_210813_1_.startsWith("eaglercraft.") && this.languageList.containsKey(p_210813_1_.substring(12));
   }

   public long getLastUpdateTimeInMilliseconds() {
      return this.lastUpdateTimeInMilliseconds;
   }

   public static Map<String, String> loadFromDump(List<String> strs) {
      Map<String, String> ret = Maps.newHashMap();
      for(int i = 0, l = strs.size(); i < l; ++i) {
         String s = strs.get(i);
         if (!s.isEmpty() && s.charAt(0) != 35) {
            int j = s.indexOf(61);
            if (j >= 0) {
               ret.put(s.substring(0, j), NUMERIC_VARIABLE_PATTERN.matcher(s.substring(j + 1)).replaceAll("%s"));
               // audrey saved me from not killing myself <3 (not literally)
            }
         }
      }
      return ret;
   }

   public static List<String> dump() {
      List<String> ret = new ArrayList<>(INSTANCE.languageList.size());
      for(Entry<String, String> etr : INSTANCE.languageList.entrySet()) {
         ret.add(etr.getKey() + "=" + etr.getValue());
      }
      return ret;
   }
}
