package net.minecraft.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.IBlockStatePalette;

public class BitArray {
   private final long[] longArray;
   private final int bitsPerEntry;
   private final long maxEntryValue;
   private final int arraySize;

   public BitArray(int bitsPerEntryIn, int arraySizeIn) {
      this(bitsPerEntryIn, arraySizeIn, new long[MathHelper.roundUp(arraySizeIn * bitsPerEntryIn, 64) / 64]);
   }

   public BitArray(int p_i47901_1_, int p_i47901_2_, long[] p_i47901_3_) {
      if (p_i47901_1_ < 1 || p_i47901_1_ > 32) {
         throw new IllegalArgumentException("Bits per entry must be between 1 and 32");
      }

      this.arraySize = p_i47901_2_;
      this.bitsPerEntry = p_i47901_1_;
      this.longArray = p_i47901_3_;
      this.maxEntryValue = (1L << p_i47901_1_) - 1L;
      int i = MathHelper.roundUp(p_i47901_2_ * p_i47901_1_, 64) / 64;
      if (p_i47901_3_.length != i) {
         throw new RuntimeException("Invalid length given for storage, got: " + p_i47901_3_.length + " but expected: " + i);
      }
   }

   public void setAt(int index, int value) {
      if (index < 0 || index >= this.arraySize) {
         throw new IndexOutOfBoundsException("Index " + index + " is outside 0-" + (this.arraySize - 1));
      }

      if (value < 0 || (long)value > this.maxEntryValue) {
         throw new IllegalArgumentException("Value " + value + " is outside 0-" + this.maxEntryValue);
      }

      int i = index * this.bitsPerEntry;
      int j = i / 64;
      int k = ((index + 1) * this.bitsPerEntry - 1) / 64;
      int l = i % 64;
      this.longArray[j] = this.longArray[j] & ~(this.maxEntryValue << l) | ((long)value & this.maxEntryValue) << l;
      if (j != k) {
         int i1 = 64 - l;
         int j1 = this.bitsPerEntry - i1;
         this.longArray[k] = this.longArray[k] >>> j1 << j1 | ((long)value & this.maxEntryValue) >> i1;
      }

   }

   public int getAt(int index) {
      if (index < 0 || index >= this.arraySize) {
         throw new IndexOutOfBoundsException("Index " + index + " is outside 0-" + (this.arraySize - 1));
      }

      int i = index * this.bitsPerEntry;
      int j = i / 64;
      int k = ((index + 1) * this.bitsPerEntry - 1) / 64;
      int l = i % 64;
      if (j == k) {
         return (int)(this.longArray[j] >>> l & this.maxEntryValue);
      } else {
         int i1 = 64 - l;
         return (int)((this.longArray[j] >>> l | this.longArray[k] << i1) & this.maxEntryValue);
      }
   }

   public <T> short[] compact(IBlockStatePalette<T> srcPalette, IBlockStatePalette<T> destPalette, T defaultState) {
      short[] ashort = new short[this.arraySize];
      short[] ashort1 = new short[this.arraySize];
      int i = this.longArray.length;
      if (i == 0) {
         return ashort;
      }

      int j = 0;
      long k = this.longArray[0];
      long l = i > 1 ? this.longArray[1] : 0L;
      int i1 = 0;
      int j1 = 0;

      while(j1 < this.arraySize) {
         int k1 = i1 >> 6;
         int l1 = i1 + this.bitsPerEntry - 1 >> 6;
         int i2 = i1 ^ k1 << 6;
         if (k1 != j) {
            k = l;
            l = k1 + 1 < i ? this.longArray[k1 + 1] : 0L;
            j = k1;
         }

         int j2;
         if (k1 == l1) {
            j2 = (int)(k >>> i2 & this.maxEntryValue);
         } else {
            j2 = (int)((k >>> i2 | l << 64 - i2) & this.maxEntryValue);
         }

         if (j2 != 0) {
            int k2 = ashort1[j2];
            if (k2 == 0) {
               T t = srcPalette.get(j2);
               if (t == null) {
                  t = defaultState;
               }

               k2 = destPalette.idFor(t) + 1;
               ashort1[j2] = (short)k2;
            }

            ashort[j1] = (short)(k2 - 1);
         }

         i1 += this.bitsPerEntry;
         ++j1;
      }

      return ashort;
   }

   public long[] getBackingLongArray() {
      return this.longArray;
   }

   public int size() {
      return this.arraySize;
   }

   public int bitsPerEntry() {
      return this.bitsPerEntry;
   }
}
