package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer {
   public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
   public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);

   public BlockDaylightDetector(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWER, Integer.valueOf(0)).with(INVERTED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return blockState.get(POWER);
   }

   public static void updatePower(IBlockState p_196319_0_, World p_196319_1_, BlockPos p_196319_2_) {
      if (p_196319_1_.dimension.hasSkyLight()) {
         int i = p_196319_1_.getLightFor(EnumLightType.SKY, p_196319_2_) - p_196319_1_.getSkylightSubtracted();
         float f = p_196319_1_.getCelestialAngleRadians(1.0F);
         boolean flag = p_196319_0_.get(INVERTED);
         if (flag) {
            i = 15 - i;
         } else if (i > 0) {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            f = f + (f1 - f) * 0.2F;
            i = Math.round((float)i * MathHelper.cos(f));
         }

         i = MathHelper.clamp(i, 0, 15);
         if (p_196319_0_.get(POWER) != i) {
            p_196319_1_.setBlockState(p_196319_2_, p_196319_0_.with(POWER, Integer.valueOf(i)), 3);
         }

      }
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (player.isAllowEdit()) {
         if (worldIn.isRemote) {
            return true;
         } else {
            IBlockState iblockstate = state.cycle(INVERTED);
            worldIn.setBlockState(pos, iblockstate, 4);
            updatePower(iblockstate, worldIn, pos);
            return true;
         }
      } else {
         return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
      }
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityDaylightDetector();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(POWER, INVERTED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }
}
