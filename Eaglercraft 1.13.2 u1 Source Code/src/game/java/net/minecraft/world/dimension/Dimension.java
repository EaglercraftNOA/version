package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Dimension {
   public static final float[] MOON_PHASE_FACTORS = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   protected World world;
   protected boolean doesWaterVaporize;
   protected boolean nether;
   protected boolean hasSkyLight;
   protected final float[] lightBrightnessTable = new float[16];
   private final float[] colorsSunriseSunset = new float[4];

   public final void setWorld(World worldIn) {
      this.world = worldIn;
      this.init();
      this.generateLightBrightnessTable();
   }

   protected void generateLightBrightnessTable() {
      float f = 0.0F;

      for(int i = 0; i <= 15; ++i) {
         float f1 = 1.0F - (float)i / 15.0F;
         this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 1.0F + 0.0F;
      }

   }

   public int getMoonPhase(long worldTime) {
      return (int)(worldTime / 24000L % 8L + 8L) % 8;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
      float f = 0.4F;
      float f1 = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F));
      float f2 = -0.0F;
      if (f1 >= -0.4F && f1 <= 0.4F) {
         float f3 = f1 / 0.4F * 0.5F + 0.5F;
         float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * (float)Math.PI)) * 0.99F;
         f4 = f4 * f4;
         this.colorsSunriseSunset[0] = f3 * 0.3F + 0.7F;
         this.colorsSunriseSunset[1] = f3 * f3 * 0.7F + 0.2F;
         this.colorsSunriseSunset[2] = f3 * f3 * 0.0F + 0.2F;
         this.colorsSunriseSunset[3] = f4;
         return this.colorsSunriseSunset;
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float getCloudHeight() {
      return 128.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSkyColored() {
      return true;
   }

   @Nullable
   public BlockPos getSpawnCoordinate() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public double getVoidFogYFactor() {
      return this.world.getWorldInfo().getGenerator() == WorldType.FLAT ? 1.0D : 0.03125D;
   }

   public boolean doesWaterVaporize() {
      return this.doesWaterVaporize;
   }

   public boolean hasSkyLight() {
      return this.hasSkyLight;
   }

   public boolean isNether() {
      return this.nether;
   }

   public float[] getLightBrightnessTable() {
      return this.lightBrightnessTable;
   }

   public WorldBorder createWorldBorder() {
      return new WorldBorder();
   }

   public void onPlayerAdded(EntityPlayerMP player) {
   }

   public void onPlayerRemoved(EntityPlayerMP player) {
   }

   public void onWorldSave() {
   }

   public void tick() {
   }

   public boolean canDropChunk(int x, int z) {
      return !this.world.isForcedChunk(x, z);
   }

   protected abstract void init();

   public abstract IChunkGenerator<?> createChunkGenerator();

   @Nullable
   public abstract BlockPos findSpawn(ChunkPos p_206920_1_, boolean checkValid);

   @Nullable
   public abstract BlockPos findSpawn(int p_206921_1_, int p_206921_2_, boolean checkValid);

   public abstract float calculateCelestialAngle(long worldTime, float partialTicks);

   public abstract boolean isSurfaceWorld();

   @OnlyIn(Dist.CLIENT)
   public abstract Vec3d getFogColor(float p_76562_1_, float p_76562_2_);

   public abstract boolean canRespawnHere();

   @OnlyIn(Dist.CLIENT)
   public abstract boolean doesXZShowFog(int x, int z);

   public abstract DimensionType getType();
}
