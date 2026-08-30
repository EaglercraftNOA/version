package net.minecraft.world.storage;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DerivedWorldInfo extends WorldInfo {
   private final WorldInfo delegate;

   public DerivedWorldInfo(WorldInfo worldInfoIn) {
      this.delegate = worldInfoIn;
   }

   public NBTTagCompound cloneNBTCompound(@Nullable NBTTagCompound nbt) {
      return this.delegate.cloneNBTCompound(nbt);
   }

   public long getSeed() {
      return this.delegate.getSeed();
   }

   public int getSpawnX() {
      return this.delegate.getSpawnX();
   }

   public int getSpawnY() {
      return this.delegate.getSpawnY();
   }

   public int getSpawnZ() {
      return this.delegate.getSpawnZ();
   }

   public long getGameTime() {
      return this.delegate.getGameTime();
   }

   public long getDayTime() {
      return this.delegate.getDayTime();
   }

   @OnlyIn(Dist.CLIENT)
   public long getSizeOnDisk() {
      return this.delegate.getSizeOnDisk();
   }

   public NBTTagCompound getPlayerNBTTagCompound() {
      return this.delegate.getPlayerNBTTagCompound();
   }

   @OnlyIn(Dist.CLIENT)
   public int getDimension() {
      return this.delegate.getDimension();
   }

   public String getWorldName() {
      return this.delegate.getWorldName();
   }

   public int getSaveVersion() {
      return this.delegate.getSaveVersion();
   }

   @OnlyIn(Dist.CLIENT)
   public long getLastTimePlayed() {
      return this.delegate.getLastTimePlayed();
   }

   public boolean isThundering() {
      return this.delegate.isThundering();
   }

   public int getThunderTime() {
      return this.delegate.getThunderTime();
   }

   public boolean isRaining() {
      return this.delegate.isRaining();
   }

   public int getRainTime() {
      return this.delegate.getRainTime();
   }

   public GameType getGameType() {
      return this.delegate.getGameType();
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnX(int x) {
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnY(int y) {
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnZ(int z) {
   }

   public void setGameTime(long time) {
   }

   public void setDayTime(long time) {
   }

   public void setSpawn(BlockPos spawnPoint) {
   }

   public void setWorldName(String worldName) {
   }

   public void setSaveVersion(int version) {
   }

   public void setThundering(boolean thunderingIn) {
   }

   public void setThunderTime(int time) {
   }

   public void setRaining(boolean isRaining) {
   }

   public void setRainTime(int time) {
   }

   public boolean isMapFeaturesEnabled() {
      return this.delegate.isMapFeaturesEnabled();
   }

   public boolean isHardcore() {
      return this.delegate.isHardcore();
   }

   public WorldType getGenerator() {
      return this.delegate.getGenerator();
   }

   public void setGenerator(WorldType type) {
   }

   public boolean areCommandsAllowed() {
      return this.delegate.areCommandsAllowed();
   }

   public void setAllowCommands(boolean allow) {
   }

   public boolean isInitialized() {
      return this.delegate.isInitialized();
   }

   public void setInitialized(boolean initializedIn) {
   }

   public GameRules getGameRulesInstance() {
      return this.delegate.getGameRulesInstance();
   }

   public EnumDifficulty getDifficulty() {
      return this.delegate.getDifficulty();
   }

   public void setDifficulty(EnumDifficulty newDifficulty) {
   }

   public boolean isDifficultyLocked() {
      return this.delegate.isDifficultyLocked();
   }

   public void setDifficultyLocked(boolean locked) {
   }

   public void setDimensionData(DimensionType dimensionIn, NBTTagCompound compound) {
      this.delegate.setDimensionData(dimensionIn, compound);
   }

   public NBTTagCompound getDimensionData(DimensionType dimensionIn) {
      return this.delegate.getDimensionData(dimensionIn);
   }
}
