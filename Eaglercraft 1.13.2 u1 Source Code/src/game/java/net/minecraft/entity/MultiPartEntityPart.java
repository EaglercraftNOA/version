package net.minecraft.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class MultiPartEntityPart extends Entity {
   public final IEntityMultiPart parent;
   public final String partName;

   public MultiPartEntityPart(IEntityMultiPart parent, String partName, float width, float height) {
      super(parent.getType(), parent.getWorld());
      this.setSize(width, height);
      this.parent = parent;
      this.partName = partName;
   }

   protected void registerData() {
   }

   protected void readAdditional(NBTTagCompound compound) {
   }

   protected void writeAdditional(NBTTagCompound compound) {
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      return this.isInvulnerableTo(source) ? false : this.parent.attackEntityFromPart(this, source, amount);
   }

   public boolean isEntityEqual(Entity entityIn) {
      return this == entityIn || this.parent == entityIn;
   }
}
