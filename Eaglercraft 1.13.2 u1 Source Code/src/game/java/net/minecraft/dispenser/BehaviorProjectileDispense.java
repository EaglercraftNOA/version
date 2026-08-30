package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {
   public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      World world = source.getWorld();
      IPosition iposition = BlockDispenser.getDispensePosition(source);
      EnumFacing enumfacing = source.getBlockState().get(BlockDispenser.FACING);
      IProjectile iprojectile = this.getProjectileEntity(world, iposition, stack);
      iprojectile.shoot((double)enumfacing.getXOffset(), (double)((float)enumfacing.getYOffset() + 0.1F), (double)enumfacing.getZOffset(), this.getProjectileVelocity(), this.getProjectileInaccuracy());
      world.spawnEntity((Entity)iprojectile);
      stack.shrink(1);
      return stack;
   }

   protected void playDispenseSound(IBlockSource source) {
      source.getWorld().playEvent(1002, source.getBlockPos(), 0);
   }

   protected abstract IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn);

   protected float getProjectileInaccuracy() {
      return 6.0F;
   }

   protected float getProjectileVelocity() {
      return 1.1F;
   }
}
