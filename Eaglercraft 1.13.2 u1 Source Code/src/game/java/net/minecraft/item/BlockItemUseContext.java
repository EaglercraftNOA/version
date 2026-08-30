package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItemUseContext extends ItemUseContext {
   private final BlockPos offsetPos;
   protected boolean replaceClicked = true;

   public BlockItemUseContext(ItemUseContext context) {
      this(context.getWorld(), context.getPlayer(), context.getItem(), context.getPos(), context.getFace(), context.getHitX(), context.getHitY(), context.getHitZ());
   }

   protected BlockItemUseContext(World worldIn, @Nullable EntityPlayer playerIn, ItemStack itemIn, BlockPos posIn, EnumFacing faceIn, float hitXIn, float hitYIn, float hitZIn) {
      super(worldIn, playerIn, itemIn, posIn, faceIn, hitXIn, hitYIn, hitZIn);
      this.offsetPos = this.pos.offset(this.face);
      this.replaceClicked = this.getWorld().getBlockState(this.pos).isReplaceable(this);
   }

   public BlockPos getPos() {
      return this.replaceClicked ? this.pos : this.offsetPos;
   }

   public boolean canPlace() {
      return this.replaceClicked || this.getWorld().getBlockState(this.getPos()).isReplaceable(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.replaceClicked;
   }

   public EnumFacing getNearestLookingDirection() {
      return EnumFacing.getFacingDirections(this.player)[0];
   }

   public EnumFacing[] getNearestLookingDirections() {
      EnumFacing[] aenumfacing = EnumFacing.getFacingDirections(this.player);
      if (this.replaceClicked) {
         return aenumfacing;
      } else {
         int i;
         for(i = 0; i < aenumfacing.length && aenumfacing[i] != this.face.getOpposite(); ++i) {
            ;
         }

         if (i > 0) {
            System.arraycopy(aenumfacing, 0, aenumfacing, 1, i);
            aenumfacing[0] = this.face.getOpposite();
         }

         return aenumfacing;
      }
   }
}
