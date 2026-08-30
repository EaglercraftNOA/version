package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityJukebox;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer {
   public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

   protected BlockJukebox(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(HAS_RECORD, Boolean.valueOf(false)));
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (state.get(HAS_RECORD)) {
         this.dropRecord(worldIn, pos);
         state = state.with(HAS_RECORD, Boolean.valueOf(false));
         worldIn.setBlockState(pos, state, 2);
         return true;
      } else {
         return false;
      }
   }

   public void insertRecord(IWorld worldIn, BlockPos pos, IBlockState state, ItemStack recordStack) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityJukebox) {
         ((TileEntityJukebox)tileentity).setRecord(recordStack.copy());
         worldIn.setBlockState(pos, state.with(HAS_RECORD, Boolean.valueOf(true)), 2);
      }
   }

   private void dropRecord(World worldIn, BlockPos pos) {
      if (!worldIn.isRemote) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityJukebox) {
            TileEntityJukebox tileentityjukebox = (TileEntityJukebox)tileentity;
            ItemStack itemstack = tileentityjukebox.getRecord();
            if (!itemstack.isEmpty()) {
               worldIn.playEvent(1010, pos, 0);
               worldIn.playRecord(pos, (SoundEvent)null);
               tileentityjukebox.setRecord(ItemStack.EMPTY);
               float f = 0.7F;
               double d0 = (double)(worldIn.rand.nextFloat() * 0.7F) + (double)0.15F;
               double d1 = (double)(worldIn.rand.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
               double d2 = (double)(worldIn.rand.nextFloat() * 0.7F) + (double)0.15F;
               ItemStack itemstack1 = itemstack.copy();
               EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, itemstack1);
               entityitem.setDefaultPickupDelay();
               worldIn.spawnEntity(entityitem);
            }
         }
      }
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         this.dropRecord(worldIn, pos);
         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      if (!worldIn.isRemote) {
         super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, 0);
      }
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityJukebox();
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityJukebox) {
         Item item = ((TileEntityJukebox)tileentity).getRecord().getItem();
         if (item instanceof ItemRecord) {
            return ((ItemRecord)item).getComparatorValue();
         }
      }

      return 0;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(HAS_RECORD);
   }
}
