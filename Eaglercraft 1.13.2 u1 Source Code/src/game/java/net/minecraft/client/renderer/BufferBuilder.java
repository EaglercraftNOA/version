package net.minecraft.client.renderer;

import java.util.BitSet;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BufferBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private ByteBuffer byteBuffer;
   private IntBuffer rawIntBuffer;
   private FloatBuffer rawFloatBuffer;
   private final int initialCapacity;
   private int vertexCount;
   private boolean noColor;
   private int drawMode;
   private double xOffset;
   private double yOffset;
   private double zOffset;
   private VertexFormat vertexFormat;
   private boolean isDrawing;
   private float[] sortArrayCacheA;
   private int[] sortArrayCacheB;
   private int[] sortCopyBuffer;
   private int[] sortMoveBuffer;
   private BitSet sortBitSetCache;

   public BufferBuilder(int bufferSizeIn) {
      this.initialCapacity = bufferSizeIn * 4;
      this.byteBuffer = EagRuntime.allocateByteBuffer(this.initialCapacity);
      this.rawIntBuffer = this.byteBuffer.asIntBuffer();
      this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
   }

   private void growBuffer(int increaseAmount) {
      int l = this.vertexCount * this.vertexFormat.attribStride + increaseAmount;
      if (l > this.byteBuffer.capacity()) {
         int i = this.byteBuffer.capacity();
         int j = Math.max(this.initialCapacity, MathHelper.roundUp(l + (increaseAmount >> 1), 262144));
         LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, j);
         int k = this.rawIntBuffer.position();
         ByteBuffer bytebuffer = EagRuntime.allocateByteBuffer(j);
         this.byteBuffer.position(0);
         bytebuffer.put(this.byteBuffer);
         bytebuffer.rewind();
         EagRuntime.freeByteBuffer(this.byteBuffer);
         this.byteBuffer = bytebuffer;
         this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
         this.rawIntBuffer = this.byteBuffer.asIntBuffer();
         this.rawIntBuffer.position(k);
      }
   }

   public void discardLargeBuffer() {
      if (!this.isDrawing && this.byteBuffer.capacity() > Math.max(this.initialCapacity << 2, 16777216)) {
         EagRuntime.freeByteBuffer(this.byteBuffer);
         this.byteBuffer = EagRuntime.allocateByteBuffer(this.initialCapacity);
         this.rawIntBuffer = this.byteBuffer.asIntBuffer();
         this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
         this.byteBuffer.limit(this.byteBuffer.capacity());
         this.vertexCount = 0;
      }
   }

   public void sortVertexData(float cameraX, float cameraY, float cameraZ) {
      int i = this.vertexCount / 4;
      if (i == 0) {
         return;
      }

      float[] afloat = this.sortArrayCacheA;
      if (afloat == null || afloat.length < i) {
         afloat = new float[i];
         this.sortArrayCacheA = afloat;
      }

      for(int j = 0; j < i; ++j) {
         afloat[j] = getDistanceSq(this.rawFloatBuffer, (float)((double)cameraX + this.xOffset), (float)((double)cameraY + this.yOffset), (float)((double)cameraZ + this.zOffset), this.vertexFormat.attribStride >> 2, j * this.vertexFormat.attribStride);
      }

      int[] ainteger = this.sortArrayCacheB;
      if (ainteger == null || ainteger.length < i) {
         ainteger = new int[i];
         this.sortArrayCacheB = ainteger;
      }

      for(int k = 0; k < i; ++k) {
         ainteger[k] = k;
      }

      this.sortArrayCacheA = afloat;
      this.sortArrayCacheB = ainteger;
      this.sortQuadIndices(0, i);
      BitSet bitset = this.sortBitSetCache;
      if (bitset == null) {
         bitset = new BitSet();
         this.sortBitSetCache = bitset;
      } else {
         bitset.clear();
      }
      int l = this.vertexFormat.attribStride;
      int[] aint = this.sortCopyBuffer;
      if (aint == null || aint.length != l) {
         aint = new int[l];
         this.sortCopyBuffer = aint;
      }
      int[] aint1 = this.sortMoveBuffer;
      if (aint1 == null || aint1.length != l) {
         aint1 = new int[l];
         this.sortMoveBuffer = aint1;
      }

      for(int i1 = bitset.nextClearBit(0); i1 < i; i1 = bitset.nextClearBit(i1 + 1)) {
         int j1 = ainteger[i1];
         if (j1 != i1) {
            this.rawIntBuffer.limit(j1 * l + l);
            this.rawIntBuffer.position(j1 * l);
            this.rawIntBuffer.get(aint);
            int k1 = j1;

            for(int l1 = ainteger[j1]; k1 != i1; l1 = ainteger[l1]) {
               this.rawIntBuffer.limit(l1 * l + l);
               this.rawIntBuffer.position(l1 * l);
               this.rawIntBuffer.get(aint1);
               this.rawIntBuffer.limit(k1 * l + l);
               this.rawIntBuffer.position(k1 * l);
               this.rawIntBuffer.put(aint1);
               bitset.set(k1);
               k1 = l1;
            }

            this.rawIntBuffer.limit(i1 * l + l);
            this.rawIntBuffer.position(i1 * l);
            this.rawIntBuffer.put(aint);
         }

         bitset.set(i1);
      }

      this.rawIntBuffer.clear();
   }

   private int compareSortIndices(int i, int j) {
      return Float.compare(this.sortArrayCacheA[this.sortArrayCacheB[j]], this.sortArrayCacheA[this.sortArrayCacheB[i]]);
   }

   private void swapSortIndices(int i, int j) {
      int k = this.sortArrayCacheB[i];
      this.sortArrayCacheB[i] = this.sortArrayCacheB[j];
      this.sortArrayCacheB[j] = k;
   }

   private void sortQuadIndices(int fromIndex, int toIndex) {
      int i;
      while((i = toIndex - fromIndex) > 16) {
         int j = toIndex - 1;
         int k = fromIndex + j >>> 1;
         int l;
         if (i <= 40) {
            int i1 = i >> 2;
            l = this.medianSortIndex(k - i1, k, k + i1);
         } else {
            int j1 = i >> 3;
            int k1 = j1 << 1;
            int l1 = this.medianSortIndex(fromIndex, fromIndex + j1, fromIndex + k1);
            int i2 = this.medianSortIndex(k - j1, k, k + j1);
            int j2 = this.medianSortIndex(j - k1, j - j1, j);
            l = this.medianSortIndex(l1, i2, j2);
         }

         this.swapSortIndices(fromIndex, l);
         int k2 = fromIndex;
         int l2 = toIndex;
         int i3 = fromIndex + 1;
         int j3 = j;

         while(true) {
            int k3;
            int l3;
            while((k3 = this.compareSortIndices(++k2, fromIndex)) < 0) {
            }

            while((l3 = this.compareSortIndices(--l2, fromIndex)) > 0) {
            }

            if (k2 >= l2) {
               if (k2 == l2 && l3 == 0) {
                  this.swapSortIndices(k2, i3);
               }
               break;
            }

            this.swapSortIndices(k2, l2);
            if (l3 == 0) {
               this.swapSortIndices(k2, i3++);
            }

            if (k3 == 0) {
               this.swapSortIndices(l2, j3--);
            }
         }

         k2 = l2 + 1;

         for(int i4 = fromIndex; i4 < i3;) {
            this.swapSortIndices(i4++, l2--);
         }

         for(int j4 = j; j4 > j3;) {
            this.swapSortIndices(j4--, k2++);
         }

         if (l2 - fromIndex < j - k2) {
            this.sortQuadIndices(fromIndex, l2 + 1);
            fromIndex = k2;
         } else {
            this.sortQuadIndices(k2, toIndex);
            toIndex = l2 + 1;
         }
      }

      this.insertionSortQuadIndices(fromIndex, toIndex);
   }

   private void insertionSortQuadIndices(int fromIndex, int toIndex) {
      for(int i = fromIndex + 1; i < toIndex;) {
         int j = i++;
         int k;

         while(this.compareSortIndices(k = j - 1, j) > 0) {
            this.swapSortIndices(k, j);
            if (k == fromIndex) {
               break;
            }

            j = k;
         }
      }
   }

   private int medianSortIndex(int i, int j, int k) {
      if (this.compareSortIndices(i, j) < 0) {
         if (this.compareSortIndices(j, k) <= 0) {
            return j;
         }

         return this.compareSortIndices(i, k) < 0 ? k : i;
      } else if (this.compareSortIndices(j, k) >= 0) {
         return j;
      } else {
         return this.compareSortIndices(i, k) < 0 ? i : k;
      }
   }

   public BufferBuilder.State getVertexState() {
      this.rawIntBuffer.rewind();
      int i = this.getBufferSize();
      this.rawIntBuffer.limit(i);
      int[] aint = new int[i];
      this.rawIntBuffer.get(aint);
      this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
      this.rawIntBuffer.position(i);
      return new BufferBuilder.State(aint, this.vertexFormat);
   }

   private int getBufferSize() {
      return this.vertexCount * (this.vertexFormat.attribStride >> 2);
   }

   private static float getDistanceSq(FloatBuffer floatBufferIn, float x, float y, float z, int integerSize, int offset) {
      float f = floatBufferIn.get(offset + integerSize * 0 + 0);
      float f1 = floatBufferIn.get(offset + integerSize * 0 + 1);
      float f2 = floatBufferIn.get(offset + integerSize * 0 + 2);
      float f3 = floatBufferIn.get(offset + integerSize * 1 + 0);
      float f4 = floatBufferIn.get(offset + integerSize * 1 + 1);
      float f5 = floatBufferIn.get(offset + integerSize * 1 + 2);
      float f6 = floatBufferIn.get(offset + integerSize * 2 + 0);
      float f7 = floatBufferIn.get(offset + integerSize * 2 + 1);
      float f8 = floatBufferIn.get(offset + integerSize * 2 + 2);
      float f9 = floatBufferIn.get(offset + integerSize * 3 + 0);
      float f10 = floatBufferIn.get(offset + integerSize * 3 + 1);
      float f11 = floatBufferIn.get(offset + integerSize * 3 + 2);
      float f12 = (f + f3 + f6 + f9) * 0.25F - x;
      float f13 = (f1 + f4 + f7 + f10) * 0.25F - y;
      float f14 = (f2 + f5 + f8 + f11) * 0.25F - z;
      return f12 * f12 + f13 * f13 + f14 * f14;
   }

   public void setVertexState(BufferBuilder.State state) {
      this.rawIntBuffer.clear();
      this.growBuffer(state.getRawBuffer().length * 4);
      this.rawIntBuffer.put(state.getRawBuffer());
      this.vertexCount = state.getVertexCount();
      this.vertexFormat = state.getVertexFormat();
   }

   public void reset() {
      this.vertexCount = 0;
      this.byteBuffer.clear();
      this.rawIntBuffer.clear();
   }

   public void begin(int glMode, VertexFormat format) {
      if (this.isDrawing) {
         throw new IllegalStateException("Already building!");
      } else {
         this.isDrawing = true;
         this.reset();
         this.drawMode = glMode;
         this.vertexFormat = format;
         this.noColor = false;
         this.byteBuffer.limit(this.byteBuffer.capacity());
      }
   }

   public BufferBuilder tex(double u, double v) {
      VertexFormat fmt = this.vertexFormat;
      int i = this.vertexCount * fmt.attribStride + fmt.attribTextureOffset;
      this.byteBuffer.putFloat(i, (float)u);
      this.byteBuffer.putFloat(i + 4, (float)v);
      return this;
   }

   public BufferBuilder lightmap(int skyLight, int blockLight) {
      VertexFormat fmt = this.vertexFormat;
      int i = this.vertexCount * fmt.attribStride + fmt.attribLightmapOffset;
      this.byteBuffer.putShort(i, (short)blockLight);
      this.byteBuffer.putShort(i + 2, (short)skyLight);
      return this;
   }

   public void putBrightness4(int vertex0, int vertex1, int vertex2, int vertex3) {
      VertexFormat fmt = this.vertexFormat;
      int j = fmt.attribStride >> 2;
      int i = (this.vertexCount - 4) * j + (fmt.attribLightmapOffset >> 2);
      this.rawIntBuffer.put(i, vertex0);
      this.rawIntBuffer.put(i + j, vertex1);
      this.rawIntBuffer.put(i + j * 2, vertex2);
      this.rawIntBuffer.put(i + j * 3, vertex3);
   }

   public void putPosition(double x, double y, double z) {
      int i = this.vertexFormat.attribStride;
      int j = (this.vertexCount - 4) * i;

      for(int k = 0; k < 4; ++k) {
         int l = j + k * i;
         int i1 = l + 4;
         int j1 = i1 + 4;
         this.byteBuffer.putFloat(l, (float)(x + this.xOffset) + this.byteBuffer.getFloat(l));
         this.byteBuffer.putFloat(i1, (float)(y + this.yOffset) + this.byteBuffer.getFloat(i1));
         this.byteBuffer.putFloat(j1, (float)(z + this.zOffset) + this.byteBuffer.getFloat(j1));
      }

   }

   private int getColorIndex(int vertexIndex) {
      return ((this.vertexCount - vertexIndex) * this.vertexFormat.attribStride + this.vertexFormat.attribColorOffset) >> 2;
   }

   public void putColorMultiplier(float red, float green, float blue, int vertexIndex) {
      int i = this.getColorIndex(vertexIndex);
      int j = -1;
      if (!this.noColor) {
         j = this.rawIntBuffer.get(i);
         int k = (int)((float)(j & 255) * red);
         int l = (int)((float)(j >>> 8 & 255) * green);
         int i1 = (int)((float)(j >>> 16 & 255) * blue);
         j = j & -16777216;
         j = j | i1 << 16 | l << 8 | k;
      }

      this.rawIntBuffer.put(i, j);
   }

   private void putColor(int argb, int vertexIndex) {
      int i = this.getColorIndex(vertexIndex);
      int j = argb >>> 16 & 255;
      int k = argb >>> 8 & 255;
      int l = argb & 255;
      int i1 = argb >>> 24 & 255;
      this.putColorRGBA(i, j, k, l, i1);
   }

   public void putColorRGB_F(float red, float green, float blue, int vertexIndex) {
      int i = this.getColorIndex(vertexIndex);
      int j = MathHelper.clamp((int)(red * 255.0F), 0, 255);
      int k = MathHelper.clamp((int)(green * 255.0F), 0, 255);
      int l = MathHelper.clamp((int)(blue * 255.0F), 0, 255);
      this.putColorRGBA(i, j, k, l, 255);
   }

   private void putColorRGBA(int index, int red, int green, int blue, int alpha) {
      this.rawIntBuffer.put(index, alpha << 24 | blue << 16 | green << 8 | red);
   }

   public void noColor() {
      this.noColor = true;
   }

   public BufferBuilder color(float red, float green, float blue, float alpha) {
      return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
   }

   public BufferBuilder color(int red, int green, int blue, int alpha) {
      if (this.noColor) {
         return this;
      } else {
         VertexFormat fmt = this.vertexFormat;
         int i = this.vertexCount * fmt.attribStride + fmt.attribColorOffset;
         this.byteBuffer.putInt(i, red | green << 8 | blue << 16 | alpha << 24);
         return this;
      }
   }

   public void addVertexData(int[] vertexData) {
      this.growBuffer(vertexData.length * 4 + this.vertexFormat.attribStride);
      this.rawIntBuffer.position(this.getBufferSize());
      this.rawIntBuffer.put(vertexData);
      this.vertexCount += vertexData.length / (this.vertexFormat.attribStride >> 2);
   }

   public void endVertex() {
      ++this.vertexCount;
      this.growBuffer(this.vertexFormat.attribStride);
   }

   public BufferBuilder pos(double x, double y, double z) {
      int i = this.vertexCount * this.vertexFormat.attribStride;
      this.byteBuffer.putFloat(i, (float)(x + this.xOffset));
      this.byteBuffer.putFloat(i + 4, (float)(y + this.yOffset));
      this.byteBuffer.putFloat(i + 8, (float)(z + this.zOffset));
      return this;
   }

   public void putNormal(float x, float y, float z) {
      int i = (byte)((int)(x * 127.0F)) & 255;
      int j = (byte)((int)(y * 127.0F)) & 255;
      int k = (byte)((int)(z * 127.0F)) & 255;
      int l = i | j << 8 | k << 16;
      VertexFormat fmt = this.vertexFormat;
      int i1 = fmt.attribStride;
      int j1 = (this.vertexCount - 4) * i1 + fmt.attribNormalOffset;
      this.byteBuffer.putInt(j1, l);
      this.byteBuffer.putInt(j1 + i1, l);
      this.byteBuffer.putInt(j1 + i1 * 2, l);
      this.byteBuffer.putInt(j1 + i1 * 3, l);
   }

   public BufferBuilder normal(float x, float y, float z) {
      VertexFormat fmt = this.vertexFormat;
      int i = this.vertexCount * fmt.attribStride + fmt.attribNormalOffset;
      this.byteBuffer.put(i, (byte)((int)(x * 127.0F) & 255));
      this.byteBuffer.put(i + 1, (byte)((int)(y * 127.0F) & 255));
      this.byteBuffer.put(i + 2, (byte)((int)(z * 127.0F) & 255));
      return this;
   }

   public void setTranslation(double x, double y, double z) {
      this.xOffset = x;
      this.yOffset = y;
      this.zOffset = z;
   }

   public void finishDrawing() {
      if (!this.isDrawing) {
         throw new IllegalStateException("Not building!");
      } else {
         this.isDrawing = false;
         this.byteBuffer.position(0);
         this.byteBuffer.limit(this.vertexCount * this.vertexFormat.attribStride);
      }
   }

   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public int getVertexCount() {
      return this.vertexCount;
   }

   public int getDrawMode() {
      return this.drawMode;
   }

   public void putColor4(int argb) {
      for(int i = 0; i < 4; ++i) {
         this.putColor(argb, i + 1);
      }

   }

   public void putColorRGB_F4(float red, float green, float blue) {
      for(int i = 0; i < 4; ++i) {
         this.putColorRGB_F(red, green, blue, i + 1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class State {
      private final int[] stateRawBuffer;
      private final VertexFormat stateVertexFormat;

      public State(int[] buffer, VertexFormat format) {
         this.stateRawBuffer = buffer;
         this.stateVertexFormat = format;
      }

      public int[] getRawBuffer() {
         return this.stateRawBuffer;
      }

      public int getVertexCount() {
         return this.stateRawBuffer.length / (this.stateVertexFormat.attribStride >> 2);
      }

      public VertexFormat getVertexFormat() {
         return this.stateVertexFormat;
      }
   }
}
