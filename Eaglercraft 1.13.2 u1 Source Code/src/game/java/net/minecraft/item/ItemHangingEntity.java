package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
   private final Class<? extends EntityHanging> hangingEntityClass;

   public ItemHangingEntity(Class<? extends EntityHanging> hangingEntityClassIn, Item.Properties builder) {
      super(builder);
      this.hangingEntityClass = hangingEntityClassIn;
   }

   public EnumActionResult onItemUse(ItemUseContext context) {
      BlockPos blockpos = context.getPos();
      EnumFacing enumfacing = context.getFace();
      BlockPos blockpos1 = blockpos.offset(enumfacing);
      EntityPlayer entityplayer = context.getPlayer();
      if (entityplayer != null && !this.canPlace(entityplayer, enumfacing, context.getItem(), blockpos1)) {
         return EnumActionResult.FAIL;
      } else {
         World world = context.getWorld();
         EntityHanging entityhanging = this.createEntity(world, blockpos1, enumfacing);
         if (entityhanging != null && entityhanging.onValidSurface()) {
            if (!world.isRemote) {
               entityhanging.playPlaceSound();
               world.spawnEntity(entityhanging);
            }

            context.getItem().shrink(1);
         }

         return EnumActionResult.SUCCESS;
      }
   }

   protected boolean canPlace(EntityPlayer p_200127_1_, EnumFacing p_200127_2_, ItemStack p_200127_3_, BlockPos p_200127_4_) {
      return !p_200127_2_.getAxis().isVertical() && p_200127_1_.canPlayerEdit(p_200127_4_, p_200127_2_, p_200127_3_);
   }

   @Nullable
   private EntityHanging createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide) {
      if (this.hangingEntityClass == EntityPainting.class) {
         return new EntityPainting(worldIn, pos, clickedSide);
      } else {
         return this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(worldIn, pos, clickedSide) : null;
      }
   }
}
