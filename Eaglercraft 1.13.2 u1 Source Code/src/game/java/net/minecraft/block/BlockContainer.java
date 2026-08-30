package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockContainer extends Block implements ITileEntityProvider {
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();

   protected BlockContainer(Block.Properties builder) {
      super(builder);
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         worldIn.removeTileEntity(pos);
      }
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (te instanceof INameable && ((INameable)te).hasCustomName()) {
         player.addStat(StatList.BLOCK_MINED.get(this));
         player.addExhaustion(0.005F);
         if (worldIn.isRemote) {
            PRIVATE_LOGGER.debug("Never going to hit this!");
            return;
         }

         int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
         Item item = this.getItemDropped(state, worldIn, pos, i).asItem();
         if (item == Items.AIR) {
            return;
         }

         ItemStack itemstack = new ItemStack(item, this.quantityDropped(state, worldIn.rand));
         itemstack.setDisplayName(((INameable)te).getCustomName());
         spawnAsEntity(worldIn, pos, itemstack);
      } else {
         super.harvestBlock(worldIn, player, pos, state, (TileEntity)null, stack);
      }

   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }
}
