package net.minecraft.client.particle;

import net.lax1dude.eaglercraft.v1_8.minecraft.IAcceleratedParticleEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleBreaking extends Particle {
   protected ParticleBreaking(World p_i47644_1_, double p_i47644_2_, double p_i47644_4_, double p_i47644_6_, double p_i47644_8_, double p_i47644_10_, double p_i47644_12_, ItemStack p_i47644_14_) {
      this(p_i47644_1_, p_i47644_2_, p_i47644_4_, p_i47644_6_, p_i47644_14_);
      this.motionX *= (double)0.1F;
      this.motionY *= (double)0.1F;
      this.motionZ *= (double)0.1F;
      this.motionX += p_i47644_8_;
      this.motionY += p_i47644_10_;
      this.motionZ += p_i47644_12_;
   }

   protected ParticleBreaking(World p_i47645_1_, double p_i47645_2_, double p_i47645_4_, double p_i47645_6_, ItemStack p_i47645_8_) {
      super(p_i47645_1_, p_i47645_2_, p_i47645_4_, p_i47645_6_, 0.0D, 0.0D, 0.0D);
      this.setParticleTexture(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(p_i47645_8_));
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.particleGravity = 1.0F;
      this.particleScale /= 2.0F;
   }

   public int getFXLayer() {
      return 1;
   }

   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = ((float)this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
      float f1 = f + 0.015609375F;
      float f2 = ((float)this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
      float f3 = f2 + 0.015609375F;
      float f4 = 0.1F * this.particleScale;
      if (this.particleTexture != null) {
         f = this.particleTexture.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
         f1 = this.particleTexture.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
         f2 = this.particleTexture.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
         f3 = this.particleTexture.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
      }

      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & '\uffff';
      int k = i & '\uffff';
      buffer.pos((double)(f5 - rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 - rotationYZ * f4 - rotationXZ * f4)).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 - rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 - rotationYZ * f4 + rotationXZ * f4)).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 + rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 + rotationYZ * f4 + rotationXZ * f4)).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
      buffer.pos((double)(f5 + rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 + rotationYZ * f4 - rotationXZ * f4)).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
   }

   public boolean renderAccelerated(IAcceleratedParticleEngine accelerator, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (this.particleTexture == null || this.particleAngle != 0.0F) {
         return false;
      }

      float f = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f1 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f2 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      int i = Math.max(1, this.particleTexture.getWidth());
      int j = Math.max(1, this.particleTexture.getHeight());
      int k = (int)((float)i * this.particleTextureJitterX * 0.25F);
      int l = (int)((float)j * this.particleTextureJitterY * 0.25F);
      accelerator.drawParticle(f, f1, f2, this.particleTexture.getOriginX() + k, this.particleTexture.getOriginY() + l, this.getBrightnessForRender(partialTicks), Math.max(1, Math.min(i, j) >> 2), 0.1F * this.particleScale, this.particleRed, this.particleGreen, this.particleBlue, 1.0F);
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<ItemParticleData> {
      public Particle makeParticle(ItemParticleData typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleBreaking(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getItemStack());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SlimeFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleBreaking(worldIn, x, y, z, new ItemStack(Items.SLIME_BALL));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SnowballFactory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticleBreaking(worldIn, x, y, z, new ItemStack(Items.SNOWBALL));
      }
   }
}
