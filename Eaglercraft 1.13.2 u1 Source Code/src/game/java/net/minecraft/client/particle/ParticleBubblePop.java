package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.minecraft.IAcceleratedParticleEngine;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleBubblePop extends Particle {
   protected ParticleBubblePop(World p_i48832_1_, double p_i48832_2_, double p_i48832_4_, double p_i48832_6_, double p_i48832_8_, double p_i48832_10_, double p_i48832_12_) {
      super(p_i48832_1_, p_i48832_2_, p_i48832_4_, p_i48832_6_, 0.0D, 0.0D, 0.0D);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.setParticleTextureIndex(256);
      this.maxAge = 4;
      this.particleGravity = 0.008F;
      this.motionX = p_i48832_8_;
      this.motionY = p_i48832_10_;
      this.motionZ = p_i48832_12_;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= (double)this.particleGravity;
      this.move(this.motionX, this.motionY, this.motionZ);
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         int i = this.age * 5 / this.maxAge;
         if (i <= 4) {
            this.setParticleTextureIndex(256 + i);
         }
      }
   }

   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = (float)this.particleTextureIndexX / 32.0F;
      float f1 = f + 0.0624375F;
      float f2 = (float)this.particleTextureIndexY / 32.0F;
      float f3 = f2 + 0.0624375F;
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
      buffer.pos((double)(f5 - rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 - rotationYZ * f4 - rotationXZ * f4)).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 - rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 - rotationYZ * f4 + rotationXZ * f4)).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 + rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 + rotationYZ * f4 + rotationXZ * f4)).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 + rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 + rotationYZ * f4 - rotationXZ * f4)).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
   }

   public boolean renderAccelerated(IAcceleratedParticleEngine accelerator, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (this.particleAngle != 0.0F) {
         return false;
      }

      float f = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f1 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f2 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      accelerator.drawParticle(f, f1, f2, this.particleTextureIndexX * 16, this.particleTextureIndexY * 16, this.getBrightnessForRender(partialTicks), 32, 0.1F * this.particleScale, this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
      return true;
   }

   public void setParticleTextureIndex(int particleTextureIndex) {
      if (this.getFXLayer() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      } else {
         this.particleTextureIndexX = 2 * particleTextureIndex % 16;
         this.particleTextureIndexY = particleTextureIndex / 16;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      @Nullable
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleBubblePop(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}
