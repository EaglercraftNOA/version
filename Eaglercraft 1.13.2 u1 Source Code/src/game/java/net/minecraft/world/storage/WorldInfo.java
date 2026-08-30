package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldInfo {
   private String versionName;
   private int versionId;
   private boolean versionSnapshot;
   public static final EnumDifficulty DEFAULT_DIFFICULTY = EnumDifficulty.NORMAL;
   private long randomSeed;
   private WorldType generator = WorldType.DEFAULT;
   private NBTTagCompound generatorOptions = new NBTTagCompound();
   @Nullable
   private String legacyCustomOptions;
   private int spawnX;
   private int spawnY;
   private int spawnZ;
   private long gameTime;
   private long dayTime;
   private long lastTimePlayed;
   private long sizeOnDisk;
   @Nullable
   private final DataFixer fixer;
   private final int field_209227_p;
   private boolean field_209228_q;
   private NBTTagCompound playerTag;
   private int dimension;
   private String levelName;
   private int saveVersion;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private GameType gameType;
   private boolean mapFeaturesEnabled;
   private boolean hardcore;
   private boolean allowCommands;
   private boolean initialized;
   private EnumDifficulty difficulty;
   private boolean difficultyLocked;
   private double borderCenterX;
   private double borderCenterZ;
   private double borderSize = 6.0E7D;
   private long borderSizeLerpTime;
   private double borderSizeLerpTarget;
   private double borderSafeZone = 5.0D;
   private double borderDamagePerBlock = 0.2D;
   private int borderWarningBlocks = 5;
   private int borderWarningTime = 15;
   private final Set<String> disabledDataPacks = Sets.newHashSet();
   private final Set<String> enabledDataPacks = Sets.newLinkedHashSet();
   private final Map<DimensionType, NBTTagCompound> dimensionData = Maps.newIdentityHashMap();
   private NBTTagCompound customBossEvents;
   private final GameRules gameRules = new GameRules();

   protected WorldInfo() {
      this.fixer = null;
      this.field_209227_p = 1631;
      this.setGeneratorOptions(new NBTTagCompound());
   }

   public WorldInfo(NBTTagCompound p_i49564_1_, DataFixer p_i49564_2_, int p_i49564_3_, @Nullable NBTTagCompound p_i49564_4_) {
      this.fixer = p_i49564_2_;
      if (p_i49564_1_.contains("Version", 10)) {
         NBTTagCompound nbttagcompound = p_i49564_1_.getCompound("Version");
         this.versionName = nbttagcompound.getString("Name");
         this.versionId = nbttagcompound.getInt("Id");
         this.versionSnapshot = nbttagcompound.getBoolean("Snapshot");
      }

      this.randomSeed = p_i49564_1_.getLong("RandomSeed");
      if (p_i49564_1_.contains("generatorName", 8)) {
         String s1 = p_i49564_1_.getString("generatorName");
         this.generator = WorldType.byName(s1);
         if (this.generator == null) {
            this.generator = WorldType.DEFAULT;
         } else if (this.generator == WorldType.CUSTOMIZED) {
            this.legacyCustomOptions = p_i49564_1_.getString("generatorOptions");
         } else if (this.generator.isVersioned()) {
            int i = 0;
            if (p_i49564_1_.contains("generatorVersion", 99)) {
               i = p_i49564_1_.getInt("generatorVersion");
            }

            this.generator = this.generator.getWorldTypeForGeneratorVersion(i);
         }

         this.setGeneratorOptions(p_i49564_1_.getCompound("generatorOptions"));
      }

      this.gameType = GameType.getByID(p_i49564_1_.getInt("GameType"));
      if (p_i49564_1_.contains("legacy_custom_options", 8)) {
         this.legacyCustomOptions = p_i49564_1_.getString("legacy_custom_options");
      }

      if (p_i49564_1_.contains("MapFeatures", 99)) {
         this.mapFeaturesEnabled = p_i49564_1_.getBoolean("MapFeatures");
      } else {
         this.mapFeaturesEnabled = true;
      }

      this.spawnX = p_i49564_1_.getInt("SpawnX");
      this.spawnY = p_i49564_1_.getInt("SpawnY");
      this.spawnZ = p_i49564_1_.getInt("SpawnZ");
      this.gameTime = p_i49564_1_.getLong("Time");
      if (p_i49564_1_.contains("DayTime", 99)) {
         this.dayTime = p_i49564_1_.getLong("DayTime");
      } else {
         this.dayTime = this.gameTime;
      }

      this.lastTimePlayed = p_i49564_1_.getLong("LastPlayed");
      this.sizeOnDisk = p_i49564_1_.getLong("SizeOnDisk");
      this.levelName = p_i49564_1_.getString("LevelName");
      this.saveVersion = p_i49564_1_.getInt("version");
      this.clearWeatherTime = p_i49564_1_.getInt("clearWeatherTime");
      this.rainTime = p_i49564_1_.getInt("rainTime");
      this.raining = p_i49564_1_.getBoolean("raining");
      this.thunderTime = p_i49564_1_.getInt("thunderTime");
      this.thundering = p_i49564_1_.getBoolean("thundering");
      this.hardcore = p_i49564_1_.getBoolean("hardcore");
      if (p_i49564_1_.contains("initialized", 99)) {
         this.initialized = p_i49564_1_.getBoolean("initialized");
      } else {
         this.initialized = true;
      }

      if (p_i49564_1_.contains("allowCommands", 99)) {
         this.allowCommands = p_i49564_1_.getBoolean("allowCommands");
      } else {
         this.allowCommands = this.gameType == GameType.CREATIVE;
      }

      this.field_209227_p = p_i49564_3_;
      if (p_i49564_4_ != null) {
         this.playerTag = p_i49564_4_;
      }

      if (p_i49564_1_.contains("GameRules", 10)) {
         this.gameRules.read(p_i49564_1_.getCompound("GameRules"));
      }

      if (p_i49564_1_.contains("Difficulty", 99)) {
         this.difficulty = EnumDifficulty.byId(p_i49564_1_.getByte("Difficulty"));
      }

      if (p_i49564_1_.contains("DifficultyLocked", 1)) {
         this.difficultyLocked = p_i49564_1_.getBoolean("DifficultyLocked");
      }

      if (p_i49564_1_.contains("BorderCenterX", 99)) {
         this.borderCenterX = p_i49564_1_.getDouble("BorderCenterX");
      }

      if (p_i49564_1_.contains("BorderCenterZ", 99)) {
         this.borderCenterZ = p_i49564_1_.getDouble("BorderCenterZ");
      }

      if (p_i49564_1_.contains("BorderSize", 99)) {
         this.borderSize = p_i49564_1_.getDouble("BorderSize");
      }

      if (p_i49564_1_.contains("BorderSizeLerpTime", 99)) {
         this.borderSizeLerpTime = p_i49564_1_.getLong("BorderSizeLerpTime");
      }

      if (p_i49564_1_.contains("BorderSizeLerpTarget", 99)) {
         this.borderSizeLerpTarget = p_i49564_1_.getDouble("BorderSizeLerpTarget");
      }

      if (p_i49564_1_.contains("BorderSafeZone", 99)) {
         this.borderSafeZone = p_i49564_1_.getDouble("BorderSafeZone");
      }

      if (p_i49564_1_.contains("BorderDamagePerBlock", 99)) {
         this.borderDamagePerBlock = p_i49564_1_.getDouble("BorderDamagePerBlock");
      }

      if (p_i49564_1_.contains("BorderWarningBlocks", 99)) {
         this.borderWarningBlocks = p_i49564_1_.getInt("BorderWarningBlocks");
      }

      if (p_i49564_1_.contains("BorderWarningTime", 99)) {
         this.borderWarningTime = p_i49564_1_.getInt("BorderWarningTime");
      }

      if (p_i49564_1_.contains("DimensionData", 10)) {
         NBTTagCompound nbttagcompound1 = p_i49564_1_.getCompound("DimensionData");

         for(String s : nbttagcompound1.keySet()) {
            this.dimensionData.put(DimensionType.getById(Integer.parseInt(s)), nbttagcompound1.getCompound(s));
         }
      }

      if (p_i49564_1_.contains("DataPacks", 10)) {
         NBTTagCompound nbttagcompound2 = p_i49564_1_.getCompound("DataPacks");
         NBTTagList nbttaglist = nbttagcompound2.getList("Disabled", 8);

         for(int k = 0; k < nbttaglist.size(); ++k) {
            this.disabledDataPacks.add(nbttaglist.getString(k));
         }

         NBTTagList nbttaglist1 = nbttagcompound2.getList("Enabled", 8);

         for(int j = 0; j < nbttaglist1.size(); ++j) {
            this.enabledDataPacks.add(nbttaglist1.getString(j));
         }
      }

      if (p_i49564_1_.contains("CustomBossEvents", 10)) {
         this.customBossEvents = p_i49564_1_.getCompound("CustomBossEvents");
      }

   }

   public WorldInfo(WorldSettings settings, String name) {
      this.fixer = null;
      this.field_209227_p = 1631;
      this.populateFromWorldSettings(settings);
      this.levelName = name;
      this.difficulty = DEFAULT_DIFFICULTY;
      this.initialized = false;
   }

   public void populateFromWorldSettings(WorldSettings settings) {
      this.randomSeed = settings.getSeed();
      this.gameType = settings.getGameType();
      this.mapFeaturesEnabled = settings.isMapFeaturesEnabled();
      this.hardcore = settings.getHardcoreEnabled();
      this.generator = settings.getTerrainType();
      this.setGeneratorOptions((NBTTagCompound)Dynamic.convert(JsonOps.INSTANCE, NBTDynamicOps.INSTANCE, settings.getGeneratorOptions()));
      this.allowCommands = settings.areCommandsAllowed();
   }

   public NBTTagCompound cloneNBTCompound(@Nullable NBTTagCompound nbt) {
      this.func_209225_Q();
      if (nbt == null) {
         nbt = this.playerTag;
      }

      NBTTagCompound nbttagcompound = new NBTTagCompound();
      this.updateTagCompound(nbttagcompound, nbt);
      return nbttagcompound;
   }

   private void updateTagCompound(NBTTagCompound nbt, NBTTagCompound playerNbt) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.putString("Name", "1.13.2");
      nbttagcompound.putInt("Id", 1631);
      nbttagcompound.putBoolean("Snapshot", false);
      nbt.put("Version", nbttagcompound);
      nbt.putInt("DataVersion", 1631);
      nbt.putLong("RandomSeed", this.randomSeed);
      nbt.putString("generatorName", this.generator.getSerialization());
      nbt.putInt("generatorVersion", this.generator.getVersion());
      if (!this.generatorOptions.isEmpty()) {
         nbt.put("generatorOptions", this.generatorOptions);
      }

      if (this.legacyCustomOptions != null) {
         nbt.putString("legacy_custom_options", this.legacyCustomOptions);
      }

      nbt.putInt("GameType", this.gameType.getID());
      nbt.putBoolean("MapFeatures", this.mapFeaturesEnabled);
      nbt.putInt("SpawnX", this.spawnX);
      nbt.putInt("SpawnY", this.spawnY);
      nbt.putInt("SpawnZ", this.spawnZ);
      nbt.putLong("Time", this.gameTime);
      nbt.putLong("DayTime", this.dayTime);
      nbt.putLong("SizeOnDisk", this.sizeOnDisk);
      nbt.putLong("LastPlayed", Util.millisecondsSinceEpoch());
      nbt.putString("LevelName", this.levelName);
      nbt.putInt("version", this.saveVersion);
      nbt.putInt("clearWeatherTime", this.clearWeatherTime);
      nbt.putInt("rainTime", this.rainTime);
      nbt.putBoolean("raining", this.raining);
      nbt.putInt("thunderTime", this.thunderTime);
      nbt.putBoolean("thundering", this.thundering);
      nbt.putBoolean("hardcore", this.hardcore);
      nbt.putBoolean("allowCommands", this.allowCommands);
      nbt.putBoolean("initialized", this.initialized);
      nbt.putDouble("BorderCenterX", this.borderCenterX);
      nbt.putDouble("BorderCenterZ", this.borderCenterZ);
      nbt.putDouble("BorderSize", this.borderSize);
      nbt.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
      nbt.putDouble("BorderSafeZone", this.borderSafeZone);
      nbt.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
      nbt.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
      nbt.putDouble("BorderWarningBlocks", (double)this.borderWarningBlocks);
      nbt.putDouble("BorderWarningTime", (double)this.borderWarningTime);
      if (this.difficulty != null) {
         nbt.putByte("Difficulty", (byte)this.difficulty.getId());
      }

      nbt.putBoolean("DifficultyLocked", this.difficultyLocked);
      nbt.put("GameRules", this.gameRules.write());
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();

      for(Entry<DimensionType, NBTTagCompound> entry : this.dimensionData.entrySet()) {
         nbttagcompound1.put(String.valueOf(entry.getKey().getId()), entry.getValue());
      }

      nbt.put("DimensionData", nbttagcompound1);
      if (playerNbt != null) {
         nbt.put("Player", playerNbt);
      }

      NBTTagCompound nbttagcompound2 = new NBTTagCompound();
      NBTTagList nbttaglist = new NBTTagList();

      for(String s : this.enabledDataPacks) {
         nbttaglist.add((INBTBase)(new NBTTagString(s)));
      }

      nbttagcompound2.put("Enabled", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(String s1 : this.disabledDataPacks) {
         nbttaglist1.add((INBTBase)(new NBTTagString(s1)));
      }

      nbttagcompound2.put("Disabled", nbttaglist1);
      nbt.put("DataPacks", nbttagcompound2);
      if (this.customBossEvents != null) {
         nbt.put("CustomBossEvents", this.customBossEvents);
      }

   }

   public long getSeed() {
      return this.randomSeed;
   }

   public int getSpawnX() {
      return this.spawnX;
   }

   public int getSpawnY() {
      return this.spawnY;
   }

   public int getSpawnZ() {
      return this.spawnZ;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public long getDayTime() {
      return this.dayTime;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   private void func_209225_Q() {
      if (!this.field_209228_q && this.playerTag != null) {
         if (this.field_209227_p < 1631) {
            if (this.fixer == null) {
               throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
            }

            this.playerTag = NBTUtil.update(this.fixer, DataFixTypes.PLAYER, this.playerTag, this.field_209227_p);
         }

         this.dimension = this.playerTag.getInt("Dimension");
         this.field_209228_q = true;
      }
   }

   public NBTTagCompound getPlayerNBTTagCompound() {
      this.func_209225_Q();
      return this.playerTag;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDimension() {
      this.func_209225_Q();
      return this.dimension;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnX(int x) {
      this.spawnX = x;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnY(int y) {
      this.spawnY = y;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnZ(int z) {
      this.spawnZ = z;
   }

   public void setGameTime(long time) {
      this.gameTime = time;
   }

   public void setDayTime(long time) {
      this.dayTime = time;
   }

   public void setSpawn(BlockPos spawnPoint) {
      this.spawnX = spawnPoint.getX();
      this.spawnY = spawnPoint.getY();
      this.spawnZ = spawnPoint.getZ();
   }

   public String getWorldName() {
      return this.levelName;
   }

   public void setWorldName(String worldName) {
      this.levelName = worldName;
   }

   public int getSaveVersion() {
      return this.saveVersion;
   }

   public void setSaveVersion(int version) {
      this.saveVersion = version;
   }

   @OnlyIn(Dist.CLIENT)
   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   public void setClearWeatherTime(int cleanWeatherTimeIn) {
      this.clearWeatherTime = cleanWeatherTimeIn;
   }

   public boolean isThundering() {
      return this.thundering;
   }

   public void setThundering(boolean thunderingIn) {
      this.thundering = thunderingIn;
   }

   public int getThunderTime() {
      return this.thunderTime;
   }

   public void setThunderTime(int time) {
      this.thunderTime = time;
   }

   public boolean isRaining() {
      return this.raining;
   }

   public void setRaining(boolean isRaining) {
      this.raining = isRaining;
   }

   public int getRainTime() {
      return this.rainTime;
   }

   public void setRainTime(int time) {
      this.rainTime = time;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public void setMapFeaturesEnabled(boolean enabled) {
      this.mapFeaturesEnabled = enabled;
   }

   public void setGameType(GameType type) {
      this.gameType = type;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public void setHardcore(boolean hardcoreIn) {
      this.hardcore = hardcoreIn;
   }

   public WorldType getGenerator() {
      return this.generator;
   }

   public void setGenerator(WorldType type) {
      this.generator = type;
   }

   public NBTTagCompound getGeneratorOptions() {
      return this.generatorOptions;
   }

   public void setGeneratorOptions(NBTTagCompound p_212242_1_) {
      this.generatorOptions = p_212242_1_;
   }

   public boolean areCommandsAllowed() {
      return this.allowCommands;
   }

   public void setAllowCommands(boolean allow) {
      this.allowCommands = allow;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(boolean initializedIn) {
      this.initialized = initializedIn;
   }

   public GameRules getGameRulesInstance() {
      return this.gameRules;
   }

   public double getBorderCenterX() {
      return this.borderCenterX;
   }

   public double getBorderCenterZ() {
      return this.borderCenterZ;
   }

   public double getBorderSize() {
      return this.borderSize;
   }

   public void setBorderSize(double size) {
      this.borderSize = size;
   }

   public long getBorderSizeLerpTime() {
      return this.borderSizeLerpTime;
   }

   public void setBorderSizeLerpTime(long time) {
      this.borderSizeLerpTime = time;
   }

   public double getBorderSizeLerpTarget() {
      return this.borderSizeLerpTarget;
   }

   public void setBorderSizeLerpTarget(double lerpSize) {
      this.borderSizeLerpTarget = lerpSize;
   }

   public void setBorderCenterZ(double posZ) {
      this.borderCenterZ = posZ;
   }

   public void setBorderCenterX(double posX) {
      this.borderCenterX = posX;
   }

   public double getBorderSafeZone() {
      return this.borderSafeZone;
   }

   public void setBorderSafeZone(double amount) {
      this.borderSafeZone = amount;
   }

   public double getBorderDamagePerBlock() {
      return this.borderDamagePerBlock;
   }

   public void setBorderDamagePerBlock(double damage) {
      this.borderDamagePerBlock = damage;
   }

   public int getBorderWarningBlocks() {
      return this.borderWarningBlocks;
   }

   public int getBorderWarningTime() {
      return this.borderWarningTime;
   }

   public void setBorderWarningBlocks(int amountOfBlocks) {
      this.borderWarningBlocks = amountOfBlocks;
   }

   public void setBorderWarningTime(int ticks) {
      this.borderWarningTime = ticks;
   }

   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(EnumDifficulty newDifficulty) {
      this.difficulty = newDifficulty;
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean locked) {
      this.difficultyLocked = locked;
   }

   public void addToCrashReport(CrashReportCategory category) {
      category.addDetail("Level seed", () -> {
         return String.valueOf(this.getSeed());
      });
      category.addDetail("Level generator", () -> {
         return String.format("ID %02d - %s, ver %d. Features enabled: %b", this.generator.getId(), this.generator.getName(), this.generator.getVersion(), this.mapFeaturesEnabled);
      });
      category.addDetail("Level generator options", () -> {
         return this.generatorOptions.toString();
      });
      category.addDetail("Level spawn location", () -> {
         return CrashReportCategory.getCoordinateInfo(this.spawnX, this.spawnY, this.spawnZ);
      });
      category.addDetail("Level time", () -> {
         return String.format("%d game time, %d day time", this.gameTime, this.dayTime);
      });
      category.addDetail("Level dimension", () -> {
         return String.valueOf(this.dimension);
      });
      category.addDetail("Level storage version", () -> {
         String s = "Unknown?";

         try {
            switch(this.saveVersion) {
            case 19132:
               s = "McRegion";
               break;
            case 19133:
               s = "Anvil";
            }
         } catch (Throwable var3) {
            ;
         }

         return String.format("0x%05X - %s", this.saveVersion, s);
      });
      category.addDetail("Level weather", () -> {
         return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering);
      });
      category.addDetail("Level game mode", () -> {
         return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameType.getName(), this.gameType.getID(), this.hardcore, this.allowCommands);
      });
   }

   public NBTTagCompound getDimensionData(DimensionType dimensionIn) {
      NBTTagCompound nbttagcompound = this.dimensionData.get(dimensionIn);
      return nbttagcompound == null ? new NBTTagCompound() : nbttagcompound;
   }

   public void setDimensionData(DimensionType dimensionIn, NBTTagCompound compound) {
      this.dimensionData.put(dimensionIn, compound);
   }

   @OnlyIn(Dist.CLIENT)
   public int getVersionId() {
      return this.versionId;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isVersionSnapshot() {
      return this.versionSnapshot;
   }

   @OnlyIn(Dist.CLIENT)
   public String getVersionName() {
      return this.versionName;
   }

   public Set<String> getDisabledDataPacks() {
      return this.disabledDataPacks;
   }

   public Set<String> getEnabledDataPacks() {
      return this.enabledDataPacks;
   }

   @Nullable
   public NBTTagCompound getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable NBTTagCompound p_201356_1_) {
      this.customBossEvents = p_201356_1_;
   }
}
