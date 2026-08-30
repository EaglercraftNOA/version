package net.minecraft.world;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldType {
   public static final WorldType[] WORLD_TYPES = new WorldType[16];
   public static final WorldType DEFAULT = (new WorldType(0, "default", 1)).setVersioned();
   public static final WorldType FLAT = (new WorldType(1, "flat")).setCustomOptions(true);
   public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
   public static final WorldType AMPLIFIED = (new WorldType(3, "amplified")).enableInfoNotice();
   public static final WorldType CUSTOMIZED = (new WorldType(4, "customized", "normal", 0)).setCustomOptions(true).setCanBeCreated(false);
   public static final WorldType BUFFET = (new WorldType(5, "buffet")).setCustomOptions(true);
   public static final WorldType DEBUG_ALL_BLOCK_STATES = new WorldType(6, "debug_all_block_states");
   public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0)).setCanBeCreated(false);
   private final int id;
   private final String name;
   private final String field_211890_l;
   private final int version;
   private boolean canBeCreated;
   private boolean versioned;
   private boolean hasInfoNotice;
   private boolean field_205395_p;

   private WorldType(int id, String name) {
      this(id, name, name, 0);
   }

   private WorldType(int id, String name, int version) {
      this(id, name, name, version);
   }

   private WorldType(int p_i49778_1_, String p_i49778_2_, String p_i49778_3_, int p_i49778_4_) {
      this.name = p_i49778_2_;
      this.field_211890_l = p_i49778_3_;
      this.version = p_i49778_4_;
      this.canBeCreated = true;
      this.id = p_i49778_1_;
      WORLD_TYPES[p_i49778_1_] = this;
   }

   public String getName() {
      return this.name;
   }

   public String getSerialization() {
      return this.field_211890_l;
   }

   @OnlyIn(Dist.CLIENT)
   public String getTranslationKey() {
      return "generator." + this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public String getInfoTranslationKey() {
      return this.getTranslationKey() + ".info";
   }

   public int getVersion() {
      return this.version;
   }

   public WorldType getWorldTypeForGeneratorVersion(int version) {
      return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomOptions() {
      return this.field_205395_p;
   }

   public WorldType setCustomOptions(boolean p_205392_1_) {
      this.field_205395_p = p_205392_1_;
      return this;
   }

   private WorldType setCanBeCreated(boolean enable) {
      this.canBeCreated = enable;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canBeCreated() {
      return this.canBeCreated;
   }

   private WorldType setVersioned() {
      this.versioned = true;
      return this;
   }

   public boolean isVersioned() {
      return this.versioned;
   }

   public static WorldType byName(String type) {
      for(WorldType worldtype : WORLD_TYPES) {
         if (worldtype != null && worldtype.name.equalsIgnoreCase(type)) {
            return worldtype;
         }
      }

      return null;
   }

   public int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasInfoNotice() {
      return this.hasInfoNotice;
   }

   private WorldType enableInfoNotice() {
      this.hasInfoNotice = true;
      return this;
   }
}
