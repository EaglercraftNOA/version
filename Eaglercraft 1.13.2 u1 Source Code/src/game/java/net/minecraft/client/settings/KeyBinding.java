package net.minecraft.client.settings;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyBinding implements Comparable<KeyBinding> {
   private static final Map<String, KeyBinding> KEYBIND_ARRAY = Maps.newHashMap();
   private static final Map<InputMappings.Input, KeyBinding> HASH = Maps.newHashMap();
   private static final Set<String> KEYBIND_SET = Sets.newHashSet();
   private static final Map<String, Integer> CATEGORY_ORDER = Util.make(Maps.newHashMap(), (p_205215_0_) -> {
      p_205215_0_.put("key.categories.movement", 1);
      p_205215_0_.put("key.categories.gameplay", 2);
      p_205215_0_.put("key.categories.inventory", 3);
      p_205215_0_.put("key.categories.creative", 4);
      p_205215_0_.put("key.categories.multiplayer", 5);
      p_205215_0_.put("key.categories.ui", 6);
      p_205215_0_.put("key.categories.misc", 7);
   });
   private final String keyDescription;
   private final InputMappings.Input keyCodeDefault;
   private final String keyCategory;
   private InputMappings.Input keyCode;
   private boolean pressed;
   private int pressTime;

   public static void onTick(InputMappings.Input p_197981_0_) {
      KeyBinding keybinding = HASH.get(p_197981_0_);
      if (keybinding != null) {
         ++keybinding.pressTime;
      }

   }

   public static void setKeyBindState(InputMappings.Input p_197980_0_, boolean p_197980_1_) {
      KeyBinding keybinding = HASH.get(p_197980_0_);
      if (keybinding != null) {
         keybinding.pressed = p_197980_1_;
      }

   }

   public static void updateKeyBindState() {
      for(KeyBinding keybinding : KEYBIND_ARRAY.values()) {
         if (keybinding.keyCode.getType() == InputMappings.Type.KEYSYM && keybinding.keyCode.getKeyCode() != -1) {
            keybinding.pressed = InputMappings.isKeyDown(keybinding.keyCode.getKeyCode());
         }
      }

   }

   public static void unPressAllKeys() {
      for(KeyBinding keybinding : KEYBIND_ARRAY.values()) {
         keybinding.unpressKey();
      }

   }

   public static void resetKeyBindingArrayAndHash() {
      HASH.clear();

      for(KeyBinding keybinding : KEYBIND_ARRAY.values()) {
         HASH.put(keybinding.keyCode, keybinding);
      }

   }

   public KeyBinding(String description, int keyCode, String category) {
      this(description, InputMappings.Type.KEYSYM, keyCode, category);
   }

   public KeyBinding(String p_i47675_1_, InputMappings.Type p_i47675_2_, int p_i47675_3_, String p_i47675_4_) {
      this.keyDescription = p_i47675_1_;
      this.keyCode = p_i47675_2_.getOrMakeInput(p_i47675_3_);
      this.keyCodeDefault = this.keyCode;
      this.keyCategory = p_i47675_4_;
      KEYBIND_ARRAY.put(p_i47675_1_, this);
      HASH.put(this.keyCode, this);
      KEYBIND_SET.add(p_i47675_4_);
   }

   public boolean isKeyDown() {
      return this.pressed;
   }

   public String getKeyCategory() {
      return this.keyCategory;
   }

   public boolean isPressed() {
      if (this.pressTime == 0) {
         return false;
      } else {
         --this.pressTime;
         return true;
      }
   }

   private void unpressKey() {
      this.pressTime = 0;
      this.pressed = false;
   }

   public String getKeyDescription() {
      return this.keyDescription;
   }

   public InputMappings.Input getDefault() {
      return this.keyCodeDefault;
   }

   public void bind(InputMappings.Input p_197979_1_) {
      this.keyCode = p_197979_1_;
   }

   public int compareTo(KeyBinding p_compareTo_1_) {
      return this.keyCategory.equals(p_compareTo_1_.keyCategory) ? I18n.format(this.keyDescription).compareTo(I18n.format(p_compareTo_1_.keyDescription)) : CATEGORY_ORDER.get(this.keyCategory).compareTo(CATEGORY_ORDER.get(p_compareTo_1_.keyCategory));
   }

   public static Supplier<String> getDisplayString(String key) {
      KeyBinding keybinding = KEYBIND_ARRAY.get(key);
      return keybinding == null ? () -> {
         return key;
      } : keybinding::func_197978_k;
   }

   public boolean func_197983_b(KeyBinding p_197983_1_) {
      return this.keyCode.equals(p_197983_1_.keyCode);
   }

   public boolean isInvalid() {
      return this.keyCode.equals(InputMappings.INPUT_INVALID);
   }

   public boolean matchesKey(int keysym, int scancode) {
      if (keysym == -1) {
         return this.keyCode.getType() == InputMappings.Type.SCANCODE && this.keyCode.getKeyCode() == scancode;
      } else {
         return this.keyCode.getType() == InputMappings.Type.KEYSYM && this.keyCode.getKeyCode() == keysym;
      }
   }

   public boolean func_197984_a(int p_197984_1_) {
      return this.keyCode.getType() == InputMappings.Type.MOUSE && this.keyCode.getKeyCode() == p_197984_1_;
   }

   public String func_197978_k() {
      return this.keyCode.getName();
   }

   public boolean func_197985_l() {
      return this.keyCode.equals(this.keyCodeDefault);
   }

   public String getTranslationKey() {
      return this.keyCode.getTranslationKey();
   }
}
