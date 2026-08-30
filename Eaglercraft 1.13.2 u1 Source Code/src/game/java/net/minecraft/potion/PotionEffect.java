package net.minecraft.potion;

import com.google.common.collect.ComparisonChain;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEffect implements Comparable<PotionEffect> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Potion potion;
   private int duration;
   private int amplifier;
   private boolean isSplashPotion;
   private boolean ambient;
   @OnlyIn(Dist.CLIENT)
   private boolean isPotionDurationMax;
   private boolean showParticles;
   private boolean showIcon;

   public PotionEffect(Potion potionIn) {
      this(potionIn, 0, 0);
   }

   public PotionEffect(Potion potionIn, int durationIn) {
      this(potionIn, durationIn, 0);
   }

   public PotionEffect(Potion potionIn, int durationIn, int amplifierIn) {
      this(potionIn, durationIn, amplifierIn, false, true);
   }

   public PotionEffect(Potion potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticlesIn) {
      this(potionIn, durationIn, amplifierIn, ambientIn, showParticlesIn, showParticlesIn);
   }

   public PotionEffect(Potion p_i48980_1_, int p_i48980_2_, int p_i48980_3_, boolean p_i48980_4_, boolean p_i48980_5_, boolean p_i48980_6_) {
      this.potion = p_i48980_1_;
      this.duration = p_i48980_2_;
      this.amplifier = p_i48980_3_;
      this.ambient = p_i48980_4_;
      this.showParticles = p_i48980_5_;
      this.showIcon = p_i48980_6_;
   }

   public PotionEffect(PotionEffect other) {
      this.potion = other.potion;
      this.duration = other.duration;
      this.amplifier = other.amplifier;
      this.ambient = other.ambient;
      this.showParticles = other.showParticles;
      this.showIcon = other.showIcon;
   }

   public boolean combine(PotionEffect other) {
      if (this.potion != other.potion) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean flag = false;
      if (other.amplifier > this.amplifier) {
         this.amplifier = other.amplifier;
         this.duration = other.duration;
         flag = true;
      } else if (other.amplifier == this.amplifier && this.duration < other.duration) {
         this.duration = other.duration;
         flag = true;
      }

      if (!other.ambient && this.ambient || flag) {
         this.ambient = other.ambient;
         flag = true;
      }

      if (other.showParticles != this.showParticles) {
         this.showParticles = other.showParticles;
         flag = true;
      }

      if (other.showIcon != this.showIcon) {
         this.showIcon = other.showIcon;
         flag = true;
      }

      return flag;
   }

   public Potion getPotion() {
      return this.potion;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean doesShowParticles() {
      return this.showParticles;
   }

   public boolean isShowIcon() {
      return this.showIcon;
   }

   public boolean tick(EntityLivingBase entityIn) {
      if (this.duration > 0) {
         if (this.potion.isReady(this.duration, this.amplifier)) {
            this.performEffect(entityIn);
         }

         this.deincrementDuration();
      }

      return this.duration > 0;
   }

   private int deincrementDuration() {
      return --this.duration;
   }

   public void performEffect(EntityLivingBase entityIn) {
      if (this.duration > 0) {
         this.potion.performEffect(entityIn, this.amplifier);
      }

   }

   public String getEffectName() {
      return this.potion.getName();
   }

   public String toString() {
      String s;
      if (this.amplifier > 0) {
         s = this.getEffectName() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         s = this.getEffectName() + ", Duration: " + this.duration;
      }

      if (this.isSplashPotion) {
         s = s + ", Splash: true";
      }

      if (!this.showParticles) {
         s = s + ", Particles: false";
      }

      if (!this.showIcon) {
         s = s + ", Show Icon: false";
      }

      return s;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof PotionEffect)) {
         return false;
      } else {
         PotionEffect potioneffect = (PotionEffect)p_equals_1_;
         return this.duration == potioneffect.duration && this.amplifier == potioneffect.amplifier && this.isSplashPotion == potioneffect.isSplashPotion && this.ambient == potioneffect.ambient && this.potion.equals(potioneffect.potion);
      }
   }

   public int hashCode() {
      int i = this.potion.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      i = 31 * i + (this.isSplashPotion ? 1 : 0);
      i = 31 * i + (this.ambient ? 1 : 0);
      return i;
   }

   public NBTTagCompound write(NBTTagCompound nbt) {
      nbt.putByte("Id", (byte)Potion.getIdFromPotion(this.getPotion()));
      nbt.putByte("Amplifier", (byte)this.getAmplifier());
      nbt.putInt("Duration", this.getDuration());
      nbt.putBoolean("Ambient", this.isAmbient());
      nbt.putBoolean("ShowParticles", this.doesShowParticles());
      nbt.putBoolean("ShowIcon", this.isShowIcon());
      return nbt;
   }

   public static PotionEffect read(NBTTagCompound nbt) {
      int i = nbt.getByte("Id");
      Potion potion = Potion.getPotionById(i);
      if (potion == null) {
         return null;
      } else {
         int j = nbt.getByte("Amplifier");
         int k = nbt.getInt("Duration");
         boolean flag = nbt.getBoolean("Ambient");
         boolean flag1 = true;
         if (nbt.contains("ShowParticles", 1)) {
            flag1 = nbt.getBoolean("ShowParticles");
         }

         boolean flag2 = flag1;
         if (nbt.contains("ShowIcon", 1)) {
            flag2 = nbt.getBoolean("ShowIcon");
         }

         return new PotionEffect(potion, k, j < 0 ? 0 : j, flag, flag1, flag2);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void setPotionDurationMax(boolean maxDuration) {
      this.isPotionDurationMax = maxDuration;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getIsPotionDurationMax() {
      return this.isPotionDurationMax;
   }

   public int compareTo(PotionEffect p_compareTo_1_) {
      int i = 32147;
      return (this.getDuration() <= 32147 || p_compareTo_1_.getDuration() <= 32147) && (!this.isAmbient() || !p_compareTo_1_.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getDuration(), p_compareTo_1_.getDuration()).compare(this.getPotion().getLiquidColor(), p_compareTo_1_.getPotion().getLiquidColor()).result() : ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getPotion().getLiquidColor(), p_compareTo_1_.getPotion().getLiquidColor()).result();
   }
}
