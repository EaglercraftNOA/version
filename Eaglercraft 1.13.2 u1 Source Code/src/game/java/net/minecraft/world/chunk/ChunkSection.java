package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTUtil;

public class ChunkSection {
   private static final IBlockStatePalette<IBlockState> field_205512_a = new BlockStatePaletteRegistry<>(Block.BLOCK_STATE_IDS, Blocks.AIR.getDefaultState());
   private final int yBase;
   private int blockRefCount;
   private int tickRefCount;
   private int fluidRefCount;
   private final BlockStateContainer<IBlockState> data;
   private NibbleArray blockLight;
   private NibbleArray skyLight;

   public ChunkSection(int y, boolean storeSkylight) {
      this.yBase = y;
      this.data = new BlockStateContainer<>(field_205512_a, Block.BLOCK_STATE_IDS, NBTUtil::readBlockState, NBTUtil::writeBlockState, Blocks.AIR.getDefaultState());
      this.blockLight = new NibbleArray();
      if (storeSkylight) {
         this.skyLight = new NibbleArray();
      }

   }

   public IBlockState get(int x, int y, int z) {
      return this.data.get(x, y, z);
   }

   public IFluidState getFluidState(int x, int y, int z) {
      return this.data.get(x, y, z).getFluidState();
   }

   public void set(int x, int y, int z, IBlockState state) {
      IBlockState iblockstate = this.get(x, y, z);
      IFluidState ifluidstate = this.getFluidState(x, y, z);
      IFluidState ifluidstate1 = state.getFluidState();
      if (!iblockstate.isAir()) {
         --this.blockRefCount;
         if (iblockstate.ticksRandomly()) {
            --this.tickRefCount;
         }
      }

      if (!ifluidstate.isEmpty()) {
         --this.fluidRefCount;
      }

      if (!state.isAir()) {
         ++this.blockRefCount;
         if (state.ticksRandomly()) {
            ++this.tickRefCount;
         }
      }

      if (!ifluidstate1.isEmpty()) {
         --this.fluidRefCount;
      }

      this.data.set(x, y, z, state);
   }

   public boolean isEmpty() {
      return this.blockRefCount == 0;
   }

   public boolean needsRandomTickAny() {
      return this.needsRandomTick() || this.needsRandomTickFluid();
   }

   public boolean needsRandomTick() {
      return this.tickRefCount > 0;
   }

   public boolean needsRandomTickFluid() {
      return this.fluidRefCount > 0;
   }

   public int getYLocation() {
      return this.yBase;
   }

   public void setSkyLight(int x, int y, int z, int value) {
      this.skyLight.set(x, y, z, value);
   }

   public int getSkyLight(int x, int y, int z) {
      return this.skyLight.get(x, y, z);
   }

   public void setBlockLight(int x, int y, int z, int value) {
      this.blockLight.set(x, y, z, value);
   }

   public int getBlockLight(int x, int y, int z) {
      return this.blockLight.get(x, y, z);
   }

   public void recalculateRefCounts() {
      this.blockRefCount = 0;
      this.tickRefCount = 0;
      this.fluidRefCount = 0;

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
               IBlockState iblockstate = this.get(i, j, k);
               IFluidState ifluidstate = this.getFluidState(i, j, k);
               if (!iblockstate.isAir()) {
                  ++this.blockRefCount;
                  if (iblockstate.ticksRandomly()) {
                     ++this.tickRefCount;
                  }
               }

               if (!ifluidstate.isEmpty()) {
                  ++this.blockRefCount;
                  if (ifluidstate.ticksRandomly()) {
                     ++this.fluidRefCount;
                  }
               }
            }
         }
      }

   }

   public BlockStateContainer<IBlockState> getData() {
      return this.data;
   }

   public NibbleArray getBlockLight() {
      return this.blockLight;
   }

   public NibbleArray getSkyLight() {
      return this.skyLight;
   }

   public void setBlockLight(NibbleArray newBlocklightArray) {
      this.blockLight = newBlocklightArray;
   }

   public void setSkyLight(NibbleArray newSkylightArray) {
      this.skyLight = newSkylightArray;
   }
}
