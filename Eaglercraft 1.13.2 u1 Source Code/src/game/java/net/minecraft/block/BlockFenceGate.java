package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockHorizontal {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
   protected static final VoxelShape AABB_HITBOX_ZAXIS = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape AABB_HITBOX_XAXIS = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
   protected static final VoxelShape AABB_HITBOX_ZAXIS_INWALL = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
   protected static final VoxelShape AABB_HITBOX_XAXIS_INWALL = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D);
   protected static final VoxelShape field_208068_x = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
   protected static final VoxelShape AABB_COLLISION_BOX_XAXIS = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);
   protected static final VoxelShape field_208069_z = VoxelShapes.or(Block.makeCuboidShape(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.makeCuboidShape(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D));
   protected static final VoxelShape AABB_COLLISION_BOX_ZAXIS = VoxelShapes.or(Block.makeCuboidShape(7.0D, 5.0D, 0.0D, 9.0D, 16.0D, 2.0D), Block.makeCuboidShape(7.0D, 5.0D, 14.0D, 9.0D, 16.0D, 16.0D));
   protected static final VoxelShape field_208066_B = VoxelShapes.or(Block.makeCuboidShape(0.0D, 2.0D, 7.0D, 2.0D, 13.0D, 9.0D), Block.makeCuboidShape(14.0D, 2.0D, 7.0D, 16.0D, 13.0D, 9.0D));
   protected static final VoxelShape field_208067_C = VoxelShapes.or(Block.makeCuboidShape(7.0D, 2.0D, 0.0D, 9.0D, 13.0D, 2.0D), Block.makeCuboidShape(7.0D, 2.0D, 14.0D, 9.0D, 13.0D, 16.0D));

   public BlockFenceGate(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(OPEN, Boolean.valueOf(false)).with(POWERED, Boolean.valueOf(false)).with(IN_WALL, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      if (state.get(IN_WALL)) {
         return state.get(HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X ? AABB_HITBOX_XAXIS_INWALL : AABB_HITBOX_ZAXIS_INWALL;
      } else {
         return state.get(HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X ? AABB_HITBOX_XAXIS : AABB_HITBOX_ZAXIS;
      }
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      EnumFacing.Axis enumfacing$axis = facing.getAxis();
      if (stateIn.get(HORIZONTAL_FACING).rotateY().getAxis() != enumfacing$axis) {
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         boolean flag = this.isWall(facingState) || this.isWall(worldIn.getBlockState(currentPos.offset(facing.getOpposite())));
         return stateIn.with(IN_WALL, Boolean.valueOf(flag));
      }
   }

   public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      if (state.get(OPEN)) {
         return VoxelShapes.empty();
      } else {
         return state.get(HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.Z ? field_208068_x : AABB_COLLISION_BOX_XAXIS;
      }
   }

   public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      if (state.get(IN_WALL)) {
         return state.get(HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X ? field_208067_C : field_208066_B;
      } else {
         return state.get(HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X ? AABB_COLLISION_BOX_ZAXIS : field_208069_z;
      }
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      switch(type) {
      case LAND:
         return state.get(OPEN);
      case WATER:
         return false;
      case AIR:
         return state.get(OPEN);
      default:
         return false;
      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      boolean flag = world.isBlockPowered(blockpos);
      EnumFacing enumfacing = context.getPlacementHorizontalFacing();
      EnumFacing.Axis enumfacing$axis = enumfacing.getAxis();
      boolean flag1 = enumfacing$axis == EnumFacing.Axis.Z && (this.isWall(world.getBlockState(blockpos.west())) || this.isWall(world.getBlockState(blockpos.east()))) || enumfacing$axis == EnumFacing.Axis.X && (this.isWall(world.getBlockState(blockpos.north())) || this.isWall(world.getBlockState(blockpos.south())));
      return this.getDefaultState().with(HORIZONTAL_FACING, enumfacing).with(OPEN, Boolean.valueOf(flag)).with(POWERED, Boolean.valueOf(flag)).with(IN_WALL, Boolean.valueOf(flag1));
   }

   private boolean isWall(IBlockState p_196380_1_) {
      return p_196380_1_.getBlock() == Blocks.COBBLESTONE_WALL || p_196380_1_.getBlock() == Blocks.MOSSY_COBBLESTONE_WALL;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (state.get(OPEN)) {
         state = state.with(OPEN, Boolean.valueOf(false));
         worldIn.setBlockState(pos, state, 10);
      } else {
         EnumFacing enumfacing = player.getHorizontalFacing();
         if (state.get(HORIZONTAL_FACING) == enumfacing.getOpposite()) {
            state = state.with(HORIZONTAL_FACING, enumfacing);
         }

         state = state.with(OPEN, Boolean.valueOf(true));
         worldIn.setBlockState(pos, state, 10);
      }

      worldIn.playEvent(player, state.get(OPEN) ? 1008 : 1014, pos, 0);
      return true;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote) {
         boolean flag = worldIn.isBlockPowered(pos);
         if (state.get(POWERED) != flag) {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag)).with(OPEN, Boolean.valueOf(flag)), 2);
            if (state.get(OPEN) != flag) {
               worldIn.playEvent((EntityPlayer)null, flag ? 1008 : 1014, pos, 0);
            }
         }

      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(HORIZONTAL_FACING, OPEN, POWERED, IN_WALL);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      if (face != EnumFacing.UP && face != EnumFacing.DOWN) {
         return state.get(HORIZONTAL_FACING).getAxis() == face.rotateY().getAxis() ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
      } else {
         return BlockFaceShape.UNDEFINED;
      }
   }
}
