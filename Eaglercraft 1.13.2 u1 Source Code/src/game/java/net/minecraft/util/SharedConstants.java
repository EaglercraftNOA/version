package net.minecraft.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.PackType;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import java.util.Date;
import net.minecraft.command.TranslatableExceptionProvider;
import net.minecraft.util.datafix.NamespacedSchema;

public class SharedConstants {
   public static final Level NETTY_LEAK_DETECTION = Level.DISABLED;
   public static boolean developmentMode;
   public static final char[] ILLEGAL_FILE_CHARACTERS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
   private static final GameVersion CURRENT_VERSION = new GameVersion() {
      public String getId() {
         return "1.13.2";
      }

      public String getName() {
         return "1.13.2";
      }

      public String getReleaseTarget() {
         return "1.13.2";
      }

      public int getWorldVersion() {
         return 1631;
      }

      public int getProtocolVersion() {
         return 404;
      }

      public int getPackVersion(PackType packType) {
         return 4;
      }

      public Date getBuildTime() {
         return new Date(1543968000000L);
      }

      public boolean isStable() {
         return true;
      }
   };

   public static int getProtocolVersion() {
      return CURRENT_VERSION.getProtocolVersion();
   }

   public static GameVersion getCurrentVersion() {
      return CURRENT_VERSION;
   }

   public static boolean isAllowedCharacter(char character) {
      return character != 167 && character >= ' ' && character != 127;
   }

   public static String filterAllowedCharacters(String input) {
      StringBuilder stringbuilder = new StringBuilder();

      for(char c0 : input.toCharArray()) {
         if (isAllowedCharacter(c0)) {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   static {
      ResourceLeakDetector.setLevel(NETTY_LEAK_DETECTION);
      CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
      CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableExceptionProvider();
      NamespacedStringType.ENSURE_NAMESPACE = NamespacedSchema::ensureNamespaced;
   }
}
