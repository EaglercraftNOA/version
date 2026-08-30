package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class VertexFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<VertexFormatElement> elements = Lists.newArrayList();
   private final List<Integer> offsets = Lists.newArrayList();
   private int[] offsetCache = new int[8];
   private int vertexSize;
   private int colorElementOffset = -1;
   private final List<Integer> uvOffsetsById = Lists.newArrayList();
   private int[] uvOffsetCache = new int[4];
   private int uvOffsetCount;
   private int normalElementOffset = -1;

   public VertexFormat(VertexFormat vertexFormatIn) {
      this();

      for(int i = 0; i < vertexFormatIn.getElementCount(); ++i) {
         this.addElement(vertexFormatIn.getElement(i));
      }

      this.vertexSize = vertexFormatIn.getSize();
   }

   public VertexFormat() {
   }

   public void clear() {
      this.elements.clear();
      this.offsets.clear();
      this.colorElementOffset = -1;
      this.uvOffsetsById.clear();
      this.uvOffsetCount = 0;
      this.normalElementOffset = -1;
      this.vertexSize = 0;
   }

   public VertexFormat addElement(VertexFormatElement element) {
      if (element.isPositionElement() && this.hasPosition()) {
         LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
         return this;
      } else {
         int i = this.elements.size();
         this.elements.add(element);
         this.offsets.add(this.vertexSize);
         this.ensureOffsetCapacity(i + 1);
         this.offsetCache[i] = this.vertexSize;
         switch(element.getUsage()) {
         case NORMAL:
            this.normalElementOffset = this.vertexSize;
            break;
         case COLOR:
            this.colorElementOffset = this.vertexSize;
            break;
         case UV:
            this.uvOffsetsById.add(element.getIndex(), this.vertexSize);
            this.rebuildUvOffsetCache();
         }

         this.vertexSize += element.getSize();
         return this;
      }
   }

   public boolean hasNormal() {
      return this.normalElementOffset >= 0;
   }

   public int getNormalOffset() {
      return this.normalElementOffset;
   }

   public boolean hasColor() {
      return this.colorElementOffset >= 0;
   }

   public int getColorOffset() {
      return this.colorElementOffset;
   }

   public boolean hasUv(int id) {
      return this.uvOffsetCount - 1 >= id;
   }

   public int getUvOffsetById(int id) {
      return this.uvOffsetCache[id];
   }

   public String toString() {
      String s = "format: " + this.elements.size() + " elements: ";

      for(int i = 0; i < this.elements.size(); ++i) {
         s = s + this.elements.get(i).toString();
         if (i != this.elements.size() - 1) {
            s = s + " ";
         }
      }

      return s;
   }

   private boolean hasPosition() {
      int i = 0;

      for(int j = this.elements.size(); i < j; ++i) {
         VertexFormatElement vertexformatelement = this.elements.get(i);
         if (vertexformatelement.isPositionElement()) {
            return true;
         }
      }

      return false;
   }

   public int getIntegerSize() {
      return this.getSize() / 4;
   }

   public int getSize() {
      return this.vertexSize;
   }

   public List<VertexFormatElement> getElements() {
      return this.elements;
   }

   public int getElementCount() {
      return this.elements.size();
   }

   public VertexFormatElement getElement(int index) {
      return this.elements.get(index);
   }

   public int getOffset(int index) {
      return this.offsetCache[index];
   }

   private void ensureOffsetCapacity(int size) {
      if (this.offsetCache.length < size) {
         int[] aint = new int[Math.max(size, this.offsetCache.length << 1)];
         System.arraycopy(this.offsetCache, 0, aint, 0, this.offsetCache.length);
         this.offsetCache = aint;
      }
   }

   private void ensureUvOffsetCapacity(int size) {
      if (this.uvOffsetCache.length < size) {
         int[] aint = new int[Math.max(size, this.uvOffsetCache.length << 1)];
         System.arraycopy(this.uvOffsetCache, 0, aint, 0, this.uvOffsetCache.length);
         this.uvOffsetCache = aint;
      }
   }

   private void rebuildUvOffsetCache() {
      this.uvOffsetCount = this.uvOffsetsById.size();
      this.ensureUvOffsetCapacity(this.uvOffsetCount);

      for(int i = 0; i < this.uvOffsetCount; ++i) {
         this.uvOffsetCache[i] = this.uvOffsetsById.get(i);
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VertexFormat vertexformat = (VertexFormat)p_equals_1_;
         if (this.vertexSize != vertexformat.vertexSize) {
            return false;
         } else {
            return !this.elements.equals(vertexformat.elements) ? false : this.offsets.equals(vertexformat.offsets);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.elements.hashCode();
      i = 31 * i + this.offsets.hashCode();
      i = 31 * i + this.vertexSize;
      return i;
   }
}
