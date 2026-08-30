package net.minecraft.client.particle;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.ReuseableStream;
import net.lax1dude.eaglercraft.v1_8.minecraft.IAcceleratedParticleEngine;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Particle {
   private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
   protected World world;
   protected double prevPosX;
   protected double prevPosY;
   protected double prevPosZ;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected double motionX;
   protected double motionY;
   protected double motionZ;
   private AxisAlignedBB boundingBox = EMPTY_AABB;
   protected boolean onGround;
   protected boolean canCollide;
   protected boolean isExpired;
   protected float width = 0.6F;
   protected float height = 1.8F;
   protected EaglercraftRandom rand = new EaglercraftRandom();
   protected int particleTextureIndexX;
   protected int particleTextureIndexY;
   protected float particleTextureJitterX;
   protected float particleTextureJitterY;
   protected int age;
   protected int maxAge;
   protected float particleScale;
   protected float particleGravity;
   protected float particleRed;
   protected float particleGreen;
   protected float particleBlue;
   protected float particleAlpha = 1.0F;
   protected TextureAtlasSprite particleTexture;
   protected float particleAngle;
   protected float prevParticleAngle;
   public static double interpPosX;
   public static double interpPosY;
   public static double interpPosZ;
   public static Vec3d cameraViewDir;

   protected Particle(World worldIn, double posXIn, double posYIn, double posZIn) {
      this.world = worldIn;
      this.setSize(0.2F, 0.2F);
      this.setPosition(posXIn, posYIn, posZIn);
      this.prevPosX = posXIn;
      this.prevPosY = posYIn;
      this.prevPosZ = posZIn;
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
      this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
      this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
      this.maxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
      this.age = 0;
      this.canCollide = true;
   }

   public Particle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      this(worldIn, xCoordIn, yCoordIn, zCoordIn);
      this.motionX = xSpeedIn + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)0.4F;
      this.motionY = ySpeedIn + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)0.4F;
      this.motionZ = zSpeedIn + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)0.4F;
      float f = (float)(this.rand.nextDouble() + this.rand.nextDouble() + 1.0D) * 0.15F;
      float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / (double)f1 * (double)f * (double)0.4F;
      this.motionY = this.motionY / (double)f1 * (double)f * (double)0.4F + (double)0.1F;
      this.motionZ = this.motionZ / (double)f1 * (double)f * (double)0.4F;
   }

   public Particle multiplyVelocity(float multiplier) {
      this.motionX *= (double)multiplier;
      this.motionY = (this.motionY - (double)0.1F) * (double)multiplier + (double)0.1F;
      this.motionZ *= (double)multiplier;
      return this;
   }

   public Particle multipleParticleScaleBy(float scale) {
      this.setSize(0.2F * scale, 0.2F * scale);
      this.particleScale *= scale;
      return this;
   }

   public void setColor(float particleRedIn, float particleGreenIn, float particleBlueIn) {
      this.particleRed = particleRedIn;
      this.particleGreen = particleGreenIn;
      this.particleBlue = particleBlueIn;
   }

   public void setAlphaF(float alpha) {
      this.particleAlpha = alpha;
   }

   public boolean shouldDisableDepth() {
      return false;
   }

   public float getRedColorF() {
      return this.particleRed;
   }

   public float getGreenColorF() {
      return this.particleGreen;
   }

   public float getBlueColorF() {
      return this.particleBlue;
   }

   public void setMaxAge(int particleLifeTime) {
      this.maxAge = particleLifeTime;
   }

   public int getMaxAge() {
      return this.maxAge;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

      this.motionY -= 0.04D * (double)this.particleGravity;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= (double)0.98F;
      this.motionY *= (double)0.98F;
      this.motionZ *= (double)0.98F;
      if (this.onGround) {
         this.motionX *= (double)0.7F;
         this.motionZ *= (double)0.7F;
      }

   }

   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = (float)this.particleTextureIndexX / 32.0F;
      float f1 = f + 0.03121875F;
      float f2 = (float)this.particleTextureIndexY / 32.0F;
      float f3 = f2 + 0.03121875F;
      float f4 = 0.1F * this.particleScale;
      if (this.particleTexture != null) {
         f = this.particleTexture.getMinU();
         f1 = this.particleTexture.getMaxU();
         f2 = this.particleTexture.getMinV();
         f3 = this.particleTexture.getMaxV();
      }

      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & '\uffff';
      int k = i & '\uffff';
      double d0 = (double)(-rotationX * f4 - rotationXY * f4);
      double d1 = (double)(-rotationZ * f4);
      double d2 = (double)(-rotationYZ * f4 - rotationXZ * f4);
      double d3 = (double)(-rotationX * f4 + rotationXY * f4);
      double d4 = (double)(rotationZ * f4);
      double d5 = (double)(-rotationYZ * f4 + rotationXZ * f4);
      double d6 = (double)(rotationX * f4 + rotationXY * f4);
      double d7 = (double)(rotationZ * f4);
      double d8 = (double)(rotationYZ * f4 + rotationXZ * f4);
      double d9 = (double)(rotationX * f4 - rotationXY * f4);
      double d10 = (double)(-rotationZ * f4);
      double d11 = (double)(rotationYZ * f4 - rotationXZ * f4);
      if (this.particleAngle != 0.0F) {
         float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
         float f9 = MathHelper.cos(f8 * 0.5F);
         float f10 = MathHelper.sin(f8 * 0.5F);
         double d12 = (double)(f10 * (float)cameraViewDir.x);
         double d13 = (double)(f10 * (float)cameraViewDir.y);
         double d14 = (double)(f10 * (float)cameraViewDir.z);
         double d15 = (double)(f9 * f9) - (d12 * d12 + d13 * d13 + d14 * d14);
         double d16 = (double)(2.0F * f9);
         double d17 = d0 * d12 + d1 * d13 + d2 * d14;
         double d18 = d13 * d2 - d14 * d1;
         double d19 = d14 * d0 - d12 * d2;
         double d20 = d12 * d1 - d13 * d0;
         double d21 = d12 * (2.0D * d17) + d0 * d15 + d18 * d16;
         double d22 = d13 * (2.0D * d17) + d1 * d15 + d19 * d16;
         double d23 = d14 * (2.0D * d17) + d2 * d15 + d20 * d16;
         d0 = d21;
         d1 = d22;
         d2 = d23;
         d17 = d3 * d12 + d4 * d13 + d5 * d14;
         d18 = d13 * d5 - d14 * d4;
         d19 = d14 * d3 - d12 * d5;
         d20 = d12 * d4 - d13 * d3;
         d21 = d12 * (2.0D * d17) + d3 * d15 + d18 * d16;
         d22 = d13 * (2.0D * d17) + d4 * d15 + d19 * d16;
         d23 = d14 * (2.0D * d17) + d5 * d15 + d20 * d16;
         d3 = d21;
         d4 = d22;
         d5 = d23;
         d17 = d6 * d12 + d7 * d13 + d8 * d14;
         d18 = d13 * d8 - d14 * d7;
         d19 = d14 * d6 - d12 * d8;
         d20 = d12 * d7 - d13 * d6;
         d21 = d12 * (2.0D * d17) + d6 * d15 + d18 * d16;
         d22 = d13 * (2.0D * d17) + d7 * d15 + d19 * d16;
         d23 = d14 * (2.0D * d17) + d8 * d15 + d20 * d16;
         d6 = d21;
         d7 = d22;
         d8 = d23;
         d17 = d9 * d12 + d10 * d13 + d11 * d14;
         d18 = d13 * d11 - d14 * d10;
         d19 = d14 * d9 - d12 * d11;
         d20 = d12 * d10 - d13 * d9;
         d21 = d12 * (2.0D * d17) + d9 * d15 + d18 * d16;
         d22 = d13 * (2.0D * d17) + d10 * d15 + d19 * d16;
         d23 = d14 * (2.0D * d17) + d11 * d15 + d20 * d16;
         d9 = d21;
         d10 = d22;
         d11 = d23;
      }

      buffer.pos((double)f5 + d0, (double)f6 + d1, (double)f7 + d2).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)f5 + d3, (double)f6 + d4, (double)f7 + d5).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)f5 + d6, (double)f6 + d7, (double)f7 + d8).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)f5 + d9, (double)f6 + d10, (double)f7 + d11).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
   }

   public int getFXLayer() {
      return 0;
   }

   public boolean renderAccelerated(IAcceleratedParticleEngine accelerator, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (this.particleAngle != 0.0F || this.getFXLayer() == 3) {
         return false;
      }

      float f = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f1 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f2 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      if (this.particleTexture != null) {
         accelerator.drawParticle(f, f1, f2, this.particleTexture.getOriginX(), this.particleTexture.getOriginY(), i, Math.min(this.particleTexture.getWidth(), this.particleTexture.getHeight()), 0.1F * this.particleScale, this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
      } else {
         accelerator.drawParticle(f, f1, f2, this.particleTextureIndexX * 16, this.particleTextureIndexY * 16, i, 16, 0.1F * this.particleScale, this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
      }

      return true;
   }

   public void setParticleTexture(TextureAtlasSprite texture) {
      int i = this.getFXLayer();
      if (i == 1) {
         this.particleTexture = texture;
      } else {
         throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
      }
   }

   public void setParticleTextureIndex(int particleTextureIndex) {
      if (this.getFXLayer() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      } else {
         this.particleTextureIndexX = particleTextureIndex % 16;
         this.particleTextureIndexY = particleTextureIndex / 16;
      }
   }

   public void nextTextureIndexX() {
      ++this.particleTextureIndexX;
   }

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.age;
   }

   public void setExpired() {
      this.isExpired = true;
   }

   protected void setSize(float particleWidth, float particleHeight) {
      if (particleWidth != this.width || particleHeight != this.height) {
         this.width = particleWidth;
         this.height = particleHeight;
         AxisAlignedBB axisalignedbb = this.getBoundingBox();
         double d0 = (axisalignedbb.minX + axisalignedbb.maxX - (double)particleWidth) / 2.0D;
         double d1 = (axisalignedbb.minZ + axisalignedbb.maxZ - (double)particleWidth) / 2.0D;
         this.setBoundingBox(new AxisAlignedBB(d0, axisalignedbb.minY, d1, d0 + (double)this.width, axisalignedbb.minY + (double)this.height, d1 + (double)this.width));
      }

   }

   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      float f = this.width / 2.0F;
      float f1 = this.height;
      this.setBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
   }

   public void move(double x, double y, double z) {
      double d0 = y;
      if (this.canCollide && (x != 0.0D || y != 0.0D || z != 0.0D)) {
         ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(this.world.getCollisionBoxes((Entity)null, this.getBoundingBox(), x, y, z));
         y = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, this.getBoundingBox(), reuseablestream.func_212761_a(), y);
         this.setBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));
         x = VoxelShapes.func_212437_a(EnumFacing.Axis.X, this.getBoundingBox(), reuseablestream.func_212761_a(), x);
         if (x != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().offset(x, 0.0D, 0.0D));
         }

         z = VoxelShapes.func_212437_a(EnumFacing.Axis.Z, this.getBoundingBox(), reuseablestream.func_212761_a(), z);
         if (z != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, z));
         }
      } else {
         this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
      }

      this.resetPositionToBB();
      this.onGround = y != y && d0 < 0.0D;
      if (x != x) {
         this.motionX = 0.0D;
      }

      if (z != z) {
         this.motionZ = 0.0D;
      }

   }

   protected void resetPositionToBB() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
      this.posY = axisalignedbb.minY;
      this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
   }

   public int getBrightnessForRender(float partialTick) {
      BlockPos.MutableBlockPos blockpos = mutableBlockPos.setPos(this.posX, this.posY, this.posZ);
      return this.world.isBlockLoaded(blockpos) ? this.world.getCombinedLight(blockpos, 0) : 0;
   }

   protected static BlockPos.MutableBlockPos getBlockPos(double x, double y, double z) {
      return mutableBlockPos.setPos(x, y, z);
   }

   public boolean isAlive() {
      return !this.isExpired;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public void setBoundingBox(AxisAlignedBB bb) {
      this.boundingBox = bb;
   }
}
