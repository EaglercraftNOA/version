package net.minecraft.realms;

import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsVertexFormat {
   private VertexFormat v;

   public RealmsVertexFormat(VertexFormat vIn) {
      this.v = vIn;
   }

   public RealmsVertexFormat from(VertexFormat p_from_1_) {
      this.v = p_from_1_;
      return this;
   }

   public VertexFormat getVertexFormat() {
      return this.v;
   }

   public boolean hasColor() {
      return this.v.attribColorEnabled;
   }

   public boolean hasNormal() {
      return this.v.attribNormalEnabled;
   }

   public int getVertexSize() {
      return this.v.attribStride;
   }

   public int getIntegerSize() {
      return this.v.attribStride >> 2;
   }

   public int getColorOffset() {
      return this.v.attribColorOffset;
   }

   public int getNormalOffset() {
      return this.v.attribNormalOffset;
   }

   public boolean equals(Object p_equals_1_) {
      return this.v.equals(p_equals_1_);
   }

   public int hashCode() {
      return this.v.hashCode();
   }

   public String toString() {
      return this.v.toString();
   }
}
