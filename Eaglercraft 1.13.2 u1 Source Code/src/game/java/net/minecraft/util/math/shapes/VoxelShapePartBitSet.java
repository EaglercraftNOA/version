package net.minecraft.util.math.shapes;

import java.util.BitSet;
import net.minecraft.util.EnumFacing;

public final class VoxelShapePartBitSet extends VoxelShapePart {
   private final BitSet bitSet;
   private int startX;
   private int startY;
   private int startZ;
   private int endX;
   private int endY;
   private int endZ;

   public VoxelShapePartBitSet(int xSizeIn, int ySizeIn, int zSizeIn) {
      this(xSizeIn, ySizeIn, zSizeIn, xSizeIn, ySizeIn, zSizeIn, 0, 0, 0);
   }

   public VoxelShapePartBitSet(int xSizeIn, int ySizeIn, int zSizeIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      super(xSizeIn, ySizeIn, zSizeIn);
      this.bitSet = new BitSet(xSizeIn * ySizeIn * zSizeIn);
      this.startX = minX;
      this.startY = minY;
      this.startZ = minZ;
      this.endX = maxX;
      this.endY = maxY;
      this.endZ = maxZ;
   }

   public VoxelShapePartBitSet(VoxelShapePart shapePart) {
      super(shapePart.xSize, shapePart.ySize, shapePart.zSize);
      if (shapePart instanceof VoxelShapePartBitSet) {
         this.bitSet = (BitSet)((VoxelShapePartBitSet)shapePart).bitSet.clone();
      } else {
         this.bitSet = new BitSet(this.xSize * this.ySize * this.zSize);

         for(int i = 0; i < this.xSize; ++i) {
            for(int j = 0; j < this.ySize; ++j) {
               for(int k = 0; k < this.zSize; ++k) {
                  if (shapePart.isFilled(i, j, k)) {
                     this.bitSet.set(this.getIndex(i, j, k));
                  }
               }
            }
         }
      }

      this.startX = shapePart.getStart(EnumFacing.Axis.X);
      this.startY = shapePart.getStart(EnumFacing.Axis.Y);
      this.startZ = shapePart.getStart(EnumFacing.Axis.Z);
      this.endX = shapePart.getEnd(EnumFacing.Axis.X);
      this.endY = shapePart.getEnd(EnumFacing.Axis.Y);
      this.endZ = shapePart.getEnd(EnumFacing.Axis.Z);
   }

   protected int getIndex(int x, int y, int z) {
      return (x * this.ySize + y) * this.zSize + z;
   }

   public boolean isFilled(int x, int y, int z) {
      return this.bitSet.get(this.getIndex(x, y, z));
   }

   public void setFilled(int x, int y, int z, boolean expandBounds, boolean filled) {
      this.bitSet.set(this.getIndex(x, y, z), filled);
      if (expandBounds && filled) {
         this.startX = Math.min(this.startX, x);
         this.startY = Math.min(this.startY, y);
         this.startZ = Math.min(this.startZ, z);
         this.endX = Math.max(this.endX, x + 1);
         this.endY = Math.max(this.endY, y + 1);
         this.endZ = Math.max(this.endZ, z + 1);
      }

   }

   public boolean isEmpty() {
      return this.bitSet.isEmpty();
   }

   public int getStart(EnumFacing.Axis axis) {
      return axis.getCoordinate(this.startX, this.startY, this.startZ);
   }

   public int getEnd(EnumFacing.Axis axis) {
      return axis.getCoordinate(this.endX, this.endY, this.endZ);
   }

   protected boolean isZAxisLineFull(int fromZ, int toZ, int x, int y) {
      if (x >= 0 && y >= 0 && fromZ >= 0) {
         if (x < this.xSize && y < this.ySize && toZ <= this.zSize) {
            return this.bitSet.nextClearBit(this.getIndex(x, y, fromZ)) >= this.getIndex(x, y, toZ);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void setZAxisLine(int fromZ, int toZ, int x, int y, boolean filled) {
      this.bitSet.set(this.getIndex(x, y, fromZ), this.getIndex(x, y, toZ), filled);
   }

   static VoxelShapePartBitSet func_197852_a(VoxelShapePart first, VoxelShapePart second, IDoubleListMerger xMerger, IDoubleListMerger yMerger, IDoubleListMerger zMerger, IBooleanFunction op) {
      int i = xMerger.func_212435_a().size() - 1, j = yMerger.func_212435_a().size() - 1, k = zMerger.func_212435_a().size() - 1;
      int[] aint = new int[i], aint1 = new int[i], aint2 = new int[j], aint3 = new int[j], aint4 = new int[k], aint5 = new int[k];
      xMerger.forMergedIndexes((p_199628_7_, p_199628_8_, p_199628_9_) -> {
         aint[p_199628_9_] = p_199628_7_;
         aint1[p_199628_9_] = p_199628_8_;
         return true;
      });
      yMerger.forMergedIndexes((p_199627_10_, p_199627_11_, p_199627_12_) -> {
         aint2[p_199627_12_] = p_199627_10_;
         aint3[p_199627_12_] = p_199627_11_;
         return true;
      });
      zMerger.forMergedIndexes((p_199629_12_, p_199629_13_, p_199629_14_) -> {
         aint4[p_199629_14_] = p_199629_12_;
         aint5[p_199629_14_] = p_199629_13_;
         return true;
      });
      VoxelShapePartBitSet voxelshapepartbitset = new VoxelShapePartBitSet(i, j, k);
      for(int l = 0; l < i; ++l) {
         for(int i1 = 0; i1 < j; ++i1) {
            for(int j1 = 0; j1 < k; ++j1) {
               if (op.apply(first.contains(aint[l], aint2[i1], aint4[j1]), second.contains(aint1[l], aint3[i1], aint5[j1]))) {
                  voxelshapepartbitset.setFilled(l, i1, j1, true, true);
               }
            }
         }
      }
      return voxelshapepartbitset;
   }
}
