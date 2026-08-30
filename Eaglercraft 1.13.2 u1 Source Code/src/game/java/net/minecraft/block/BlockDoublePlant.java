package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockDoublePlant extends BlockBush {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

   public BlockDoublePlant(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(HALF, DoubleBlockHalf.LOWER));
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
      if (facing.getAxis() != EnumFacing.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == EnumFacing.UP) || facingState.getBlock() == this && facingState.get(HALF) != doubleblockhalf) {
         return doubleblockhalf == DoubleBlockHalf.LOWER && facing == EnumFacing.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         return Blocks.AIR.getDefaultState();
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      BlockPos blockpos = context.getPos();
      return blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context) ? super.getStateForPlacement(context) : null;
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), 3);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      if (state.get(HALF) != DoubleBlockHalf.UPPER) {
         return super.isValidPosition(state, worldIn, pos);
      } else {
         IBlockState iblockstate = worldIn.getBlockState(pos.down());
         return iblockstate.getBlock() == this && iblockstate.get(HALF) == DoubleBlockHalf.LOWER;
      }
   }

   public void placeAt(IWorld p_196390_1_, BlockPos p_196390_2_, int flags) {
      p_196390_1_.setBlockState(p_196390_2_, this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER), flags);
      p_196390_1_.setBlockState(p_196390_2_.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), flags);
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
   }

   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      DoubleBlockHalf doubleblockhalf = state.get(HALF);
      boolean flag = doubleblockhalf == DoubleBlockHalf.LOWER;
      BlockPos blockpos = flag ? pos.up() : pos.down();
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      if (iblockstate.getBlock() == this && iblockstate.get(HALF) != doubleblockhalf) {
         worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
         worldIn.playEvent(player, 2001, blockpos, Block.getStateId(iblockstate));
         if (!worldIn.isRemote && !player.isCreative()) {
            if (flag) {
               this.harvest(state, worldIn, pos, player.getHeldItemMainhand());
            } else {
               this.harvest(iblockstate, worldIn, blockpos, player.getHeldItemMainhand());
            }
         }
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   protected void harvest(IBlockState p_196391_1_, World p_196391_2_, BlockPos p_196391_3_, ItemStack p_196391_4_) {
      p_196391_1_.dropBlockAsItem(p_196391_2_, p_196391_3_, 0);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return (IItemProvider)(state.get(HALF) == DoubleBlockHalf.LOWER ? super.getItemDropped(state, worldIn, pos, fortune) : Items.AIR);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(HALF);
   }

   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.XZ;
   }

   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(IBlockState state, BlockPos pos) {
      return MathHelper.getCoordinateRandom(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
   }
}
