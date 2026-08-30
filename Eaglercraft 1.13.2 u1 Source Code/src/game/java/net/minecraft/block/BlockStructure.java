package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockStructure extends BlockContainer {
   public static final EnumProperty<StructureMode> MODE = BlockStateProperties.STRUCTURE_BLOCK_MODE;

   protected BlockStructure(Block.Properties properties) {
      super(properties);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityStructure();
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity instanceof TileEntityStructure ? ((TileEntityStructure)tileentity).usedBy(player) : false;
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
      if (!worldIn.isRemote) {
         if (placer != null) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityStructure) {
               ((TileEntityStructure)tileentity).createdBy(placer);
            }
         }

      }
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(MODE, StructureMode.DATA);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(MODE);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityStructure) {
            TileEntityStructure tileentitystructure = (TileEntityStructure)tileentity;
            boolean flag = worldIn.isBlockPowered(pos);
            boolean flag1 = tileentitystructure.isPowered();
            if (flag && !flag1) {
               tileentitystructure.setPowered(true);
               this.trigger(tileentitystructure);
            } else if (!flag && flag1) {
               tileentitystructure.setPowered(false);
            }

         }
      }
   }

   private void trigger(TileEntityStructure structureIn) {
      switch(structureIn.getMode()) {
      case SAVE:
         structureIn.save(false);
         break;
      case LOAD:
         structureIn.load(false);
         break;
      case CORNER:
         structureIn.unloadStructure();
      case DATA:
      }

   }
}
