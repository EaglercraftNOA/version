package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IWorld extends IWorldReaderBase, ISaveDataAccess, IWorldWriter {
   long getSeed();

   default float getCurrentMoonPhaseFactor() {
      return Dimension.MOON_PHASE_FACTORS[this.getDimension().getMoonPhase(this.getWorldInfo().getDayTime())];
   }

   default float getCelestialAngle(float partialTicks) {
      return this.getDimension().calculateCelestialAngle(this.getWorldInfo().getDayTime(), partialTicks);
   }

   @OnlyIn(Dist.CLIENT)
   default int getMoonPhase() {
      return this.getDimension().getMoonPhase(this.getWorldInfo().getDayTime());
   }

   ITickList<Block> getPendingBlockTicks();

   ITickList<Fluid> getPendingFluidTicks();

   default IChunk getChunkDefault(BlockPos pos) {
      return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }

   IChunk getChunk(int chunkX, int chunkZ);

   World getWorld();

   WorldInfo getWorldInfo();

   DifficultyInstance getDifficultyForLocation(BlockPos pos);

   default EnumDifficulty getDifficulty() {
      return this.getWorldInfo().getDifficulty();
   }

   IChunkProvider getChunkProvider();

   ISaveHandler getSaveHandler();

   Random getRandom();

   void notifyNeighbors(BlockPos pos, Block blockIn);

   BlockPos getSpawnPoint();

   void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch);

   void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);
}
