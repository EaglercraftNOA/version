package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmptyChunk extends Chunk {
   private static final Biome[] BIOMES = Util.make(new Biome[256], (p_203406_0_) -> {
      Arrays.fill(p_203406_0_, Biomes.PLAINS);
   });

   public EmptyChunk(World worldIn, int x, int z) {
      super(worldIn, x, z, BIOMES);
   }

   public boolean isAtLocation(int x, int z) {
      return x == this.x && z == this.z;
   }

   public void generateHeightMap() {
   }

   public void generateSkylightMap() {
   }

   public IBlockState getBlockState(BlockPos pos) {
      return Blocks.VOID_AIR.getDefaultState();
   }

   public int getLight(EnumLightType lightType, BlockPos pos, boolean hasSkylight) {
      return lightType.defaultLightValue;
   }

   public void setLightFor(EnumLightType light, boolean hasSkylight, BlockPos pos, int lightValue) {
   }

   public int getLightSubtracted(BlockPos pos, int amount, boolean hasSkylight) {
      return 0;
   }

   public void addEntity(Entity entityIn) {
   }

   public void removeEntity(Entity entityIn) {
   }

   public void removeEntityAtIndex(Entity entityIn, int index) {
   }

   public boolean canSeeSky(BlockPos pos) {
      return false;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType creationMode) {
      return null;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
   }

   public void removeTileEntity(BlockPos pos) {
   }

   public void onLoad() {
   }

   public void onUnload() {
   }

   public void markDirty() {
   }

   public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> filter) {
   }

   public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter) {
   }

   public boolean needsSaving(boolean p_76601_1_) {
      return false;
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean isEmptyBetween(int startY, int endY) {
      return true;
   }
}
