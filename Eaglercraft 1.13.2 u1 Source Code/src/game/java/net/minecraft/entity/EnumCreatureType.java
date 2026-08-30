package net.minecraft.entity;

import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.IAnimal;

public enum EnumCreatureType {
   MONSTER(IMob.class, 70, false, false),
   CREATURE(EntityAnimal.class, 10, true, true),
   AMBIENT(EntityAmbientCreature.class, 15, true, false),
   WATER_CREATURE(EntityWaterMob.class, 15, true, false);

   private final Class<? extends IAnimal> baseClass;
   private final int maxNumberOfCreature;
   private final boolean isPeacefulCreature;
   private final boolean isAnimal;

   private EnumCreatureType(Class<? extends IAnimal> p_i47849_3_, int p_i47849_4_, boolean p_i47849_5_, boolean p_i47849_6_) {
      this.baseClass = p_i47849_3_;
      this.maxNumberOfCreature = p_i47849_4_;
      this.isPeacefulCreature = p_i47849_5_;
      this.isAnimal = p_i47849_6_;
   }

   public Class<? extends IAnimal> getBaseClass() {
      return this.baseClass;
   }

   public int getMaxNumberOfCreature() {
      return this.maxNumberOfCreature;
   }

   public boolean getPeacefulCreature() {
      return this.isPeacefulCreature;
   }

   public boolean getAnimal() {
      return this.isAnimal;
   }
}
