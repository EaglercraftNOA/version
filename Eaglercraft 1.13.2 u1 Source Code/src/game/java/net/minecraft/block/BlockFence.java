package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockFence extends BlockFourWay {
   private final VoxelShape[] renderShapes;

   public BlockFence(Block.Properties properties) {
      super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
      this.renderShapes = this.func_196408_a(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
   }

   public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return this.renderShapes[this.getIndex(state)];
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   public boolean attachesTo(IBlockState p_196416_1_, BlockFaceShape p_196416_2_) {
      Block block = p_196416_1_.getBlock();
      boolean flag = p_196416_2_ == BlockFaceShape.MIDDLE_POLE && (p_196416_1_.getMaterial() == this.material || block instanceof BlockFenceGate);
      return !isExcepBlockForAttachWithPiston(block) && p_196416_2_ == BlockFaceShape.SOLID || flag;
   }

   public static boolean isExcepBlockForAttachWithPiston(Block p_194142_0_) {
      return Block.isExceptBlockForAttachWithPiston(p_194142_0_) || p_194142_0_ == Blocks.BARRIER || p_194142_0_ == Blocks.MELON || p_194142_0_ == Blocks.PUMPKIN || p_194142_0_ == Blocks.CARVED_PUMPKIN || p_194142_0_ == Blocks.JACK_O_LANTERN || p_194142_0_ == Blocks.FROSTED_ICE || p_194142_0_ == Blocks.TNT;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         return ItemLead.attachToFence(player, worldIn, pos);
      } else {
         ItemStack itemstack = player.getHeldItem(hand);
         return itemstack.getItem() == Items.LEAD || itemstack.isEmpty();
      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      BlockPos blockpos1 = blockpos.north();
      BlockPos blockpos2 = blockpos.east();
      BlockPos blockpos3 = blockpos.south();
      BlockPos blockpos4 = blockpos.west();
      IBlockState iblockstate = iblockreader.getBlockState(blockpos1);
      IBlockState iblockstate1 = iblockreader.getBlockState(blockpos2);
      IBlockState iblockstate2 = iblockreader.getBlockState(blockpos3);
      IBlockState iblockstate3 = iblockreader.getBlockState(blockpos4);
      return super.getStateForPlacement(context).with(NORTH, Boolean.valueOf(this.attachesTo(iblockstate, iblockstate.getBlockFaceShape(iblockreader, blockpos1, EnumFacing.SOUTH)))).with(EAST, Boolean.valueOf(this.attachesTo(iblockstate1, iblockstate1.getBlockFaceShape(iblockreader, blockpos2, EnumFacing.WEST)))).with(SOUTH, Boolean.valueOf(this.attachesTo(iblockstate2, iblockstate2.getBlockFaceShape(iblockreader, blockpos3, EnumFacing.NORTH)))).with(WEST, Boolean.valueOf(this.attachesTo(iblockstate3, iblockstate3.getBlockFaceShape(iblockreader, blockpos4, EnumFacing.EAST)))).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return facing.getAxis().getPlane() == EnumFacing.Plane.HORIZONTAL ? stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(this.attachesTo(facingState, facingState.getBlockFaceShape(worldIn, facingPos, facing.getOpposite())))) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
   }
}
