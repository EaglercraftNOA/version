package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFlowingFluid extends Block implements IBucketPickupHandler {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_15;
   protected final FlowingFluid fluid;
   private final List<IFluidState> field_212565_c;
   private final Map<IBlockState, VoxelShape> stateToShapeCache = Maps.newIdentityHashMap();

   protected BlockFlowingFluid(FlowingFluid fluidIn, Block.Properties builder) {
      super(builder);
      this.fluid = fluidIn;
      this.field_212565_c = Lists.newArrayList();
      this.field_212565_c.add(fluidIn.getStillFluidState(false));

      for(int i = 1; i < 8; ++i) {
         this.field_212565_c.add(fluidIn.getFlowingFluidState(8 - i, false));
      }

      this.field_212565_c.add(fluidIn.getFlowingFluidState(8, true));
      this.setDefaultState(this.stateContainer.getBaseState().with(LEVEL, Integer.valueOf(0)));
   }

   public void randomTick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      worldIn.getFluidState(pos).randomTick(worldIn, pos, random);
   }

   public boolean propagatesSkylightDown(IBlockState state, IBlockReader reader, BlockPos pos) {
      return false;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return !this.fluid.isIn(FluidTags.LAVA);
   }

   public IFluidState getFluidState(IBlockState state) {
      int i = state.get(LEVEL);
      return this.field_212565_c.get(Math.min(i, 8));
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isCollidable(IBlockState state) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(IBlockState state, IBlockState adjacentBlockState, EnumFacing side) {
      return adjacentBlockState.getFluidState().getFluid().isEquivalentTo(this.fluid) ? true : super.isSolid(state);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      IFluidState ifluidstate = worldIn.getFluidState(pos.up());
      return ifluidstate.getFluid().isEquivalentTo(this.fluid) ? VoxelShapes.fullCube() : this.stateToShapeCache.computeIfAbsent(state, (p_209903_0_) -> {
         IFluidState ifluidstate1 = p_209903_0_.getFluidState();
         return VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double)ifluidstate1.getHeight(), 1.0D);
      });
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   public int tickRate(IWorldReaderBase worldIn) {
      return this.fluid.getTickRate(worldIn);
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (this.reactWithNeighbors(worldIn, pos, state)) {
         worldIn.getPendingFluidTicks().scheduleTick(pos, state.getFluidState().getFluid(), this.tickRate(worldIn));
      }

   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.getFluidState().isSource() || facingState.getFluidState().isSource()) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, stateIn.getFluidState().getFluid(), this.tickRate(worldIn));
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (this.reactWithNeighbors(worldIn, pos, state)) {
         worldIn.getPendingFluidTicks().scheduleTick(pos, state.getFluidState().getFluid(), this.tickRate(worldIn));
      }

   }

   public boolean reactWithNeighbors(World worldIn, BlockPos pos, IBlockState state) {
      if (this.fluid.isIn(FluidTags.LAVA)) {
         boolean flag = false;

         for(EnumFacing enumfacing : EnumFacing._VALUES) {
            if (enumfacing != EnumFacing.DOWN && worldIn.getFluidState(pos.offset(enumfacing)).isTagged(FluidTags.WATER)) {
               flag = true;
               break;
            }
         }

         if (flag) {
            IFluidState ifluidstate = worldIn.getFluidState(pos);
            if (ifluidstate.isSource()) {
               worldIn.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
               this.triggerMixEffects(worldIn, pos);
               return false;
            }

            if (ifluidstate.getHeight() >= 0.44444445F) {
               worldIn.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
               this.triggerMixEffects(worldIn, pos);
               return false;
            }
         }
      }

      return true;
   }

   protected void triggerMixEffects(IWorld worldIn, BlockPos pos) {
      double d0 = (double)pos.getX();
      double d1 = (double)pos.getY();
      double d2 = (double)pos.getZ();
      worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.getRandom().nextFloat() - worldIn.getRandom().nextFloat()) * 0.8F);

      for(int i = 0; i < 8; ++i) {
         worldIn.addParticle(Particles.LARGE_SMOKE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(LEVEL);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
      if (state.get(LEVEL) == 0) {
         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
         return this.fluid;
      } else {
         return Fluids.EMPTY;
      }
   }
}
