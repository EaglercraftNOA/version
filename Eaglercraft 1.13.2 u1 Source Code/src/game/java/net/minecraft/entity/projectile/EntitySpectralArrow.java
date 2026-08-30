package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntitySpectralArrow extends EntityArrow {
   private int duration = 200;

   public EntitySpectralArrow(World worldIn) {
      super(EntityType.SPECTRAL_ARROW, worldIn);
   }

   public EntitySpectralArrow(World worldIn, EntityLivingBase shooter) {
      super(EntityType.SPECTRAL_ARROW, shooter, worldIn);
   }

   public EntitySpectralArrow(World worldIn, double x, double y, double z) {
      super(EntityType.SPECTRAL_ARROW, x, y, z, worldIn);
   }

   public void tick() {
      super.tick();
      if (this.world.isRemote && !this.inGround) {
         this.world.addParticle(Particles.INSTANT_EFFECT, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
      }

   }

   protected ItemStack getArrowStack() {
      return new ItemStack(Items.SPECTRAL_ARROW);
   }

   protected void arrowHit(EntityLivingBase living) {
      super.arrowHit(living);
      PotionEffect potioneffect = new PotionEffect(MobEffects.GLOWING, this.duration, 0);
      living.addPotionEffect(potioneffect);
   }

   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      if (compound.contains("Duration")) {
         this.duration = compound.getInt("Duration");
      }

   }

   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.putInt("Duration", this.duration);
   }
}
