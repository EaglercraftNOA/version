package net.minecraft.client.renderer;

import java.util.Arrays;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Vector4f {
   private final float[] components;

   public Vector4f(Vector4f vec) {
      this.components = Arrays.copyOf(vec.components, 4);
   }

   public Vector4f() {
      this.components = new float[4];
   }

   public Vector4f(float x, float y, float z, float w) {
      this.components = new float[]{x, y, z, w};
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Vector4f vector4f = (Vector4f)p_equals_1_;
         return Arrays.equals(this.components, vector4f.components);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.components);
   }

   public float getX() {
      return this.components[0];
   }

   public float getY() {
      return this.components[1];
   }

   public float getZ() {
      return this.components[2];
   }

   public float getW() {
      return this.components[3];
   }

   public void scale(Vector3f vec) {
      this.components[0] *= vec.getX();
      this.components[1] *= vec.getY();
      this.components[2] *= vec.getZ();
   }

   public void set(float x, float y, float z, float w) {
      this.components[0] = x;
      this.components[1] = y;
      this.components[2] = z;
      this.components[3] = w;
   }

   public void mul(Matrix4f matrix) {
      float f = this.components[0];
      float f1 = this.components[1];
      float f2 = this.components[2];
      float f3 = this.components[3];
      this.components[0] = matrix.get(0, 0) * f + matrix.get(0, 1) * f1 + matrix.get(0, 2) * f2 + matrix.get(0, 3) * f3;
      this.components[1] = matrix.get(1, 0) * f + matrix.get(1, 1) * f1 + matrix.get(1, 2) * f2 + matrix.get(1, 3) * f3;
      this.components[2] = matrix.get(2, 0) * f + matrix.get(2, 1) * f1 + matrix.get(2, 2) * f2 + matrix.get(2, 3) * f3;
      this.components[3] = matrix.get(3, 0) * f + matrix.get(3, 1) * f1 + matrix.get(3, 2) * f2 + matrix.get(3, 3) * f3;
   }

   public void func_195912_a(Quaternion quaternionIn) {
      Quaternion quaternion = new Quaternion(quaternionIn);
      quaternion.multiply(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
      Quaternion quaternion1 = new Quaternion(quaternionIn);
      quaternion1.conjugate();
      quaternion.multiply(quaternion1);
      this.set(quaternion.getX(), quaternion.getY(), quaternion.getZ(), this.getW());
   }
}
