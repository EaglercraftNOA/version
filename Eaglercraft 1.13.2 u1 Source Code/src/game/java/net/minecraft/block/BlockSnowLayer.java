package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockSnowLayer extends Block {
   public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS_1_8;
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{VoxelShapes.empty(), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

   protected BlockSnowLayer(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(LAYERS, Integer.valueOf(1)));
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      switch(type) {
      case LAND:
         return state.get(LAYERS) < 5;
      case WATER:
         return false;
      case AIR:
         return false;
      default:
         return false;
      }
   }

   public boolean isFullCube(IBlockState state) {
      return state.get(LAYERS) == 8;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPES[state.get(LAYERS)];
   }

   public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPES[state.get(LAYERS) - 1];
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.down());
      Block block = iblockstate.getBlock();
      if (block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
         BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP);
         return blockfaceshape == BlockFaceShape.SOLID || iblockstate.isIn(BlockTags.LEAVES) || block == this && iblockstate.get(LAYERS) == 8;
      } else {
         return false;
      }
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      Integer integer = state.get(LAYERS);
      if (this.canSilkHarvest() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
         if (integer == 8) {
            spawnAsEntity(worldIn, pos, new ItemStack(Blocks.SNOW_BLOCK));
         } else {
            for(int i = 0; i < integer; ++i) {
               spawnAsEntity(worldIn, pos, this.getSilkTouchDrop(state));
            }
         }
      } else {
         spawnAsEntity(worldIn, pos, new ItemStack(Items.SNOWBALL, integer));
      }

      worldIn.removeBlock(pos);
      player.addStat(StatList.BLOCK_MINED.get(this));
      player.addExhaustion(0.005F);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (worldIn.getLightFor(EnumLightType.BLOCK, pos) > 11) {
         state.dropBlockAsItem(worldIn, pos, 0);
         worldIn.removeBlock(pos);
      }

   }

   public boolean isReplaceable(IBlockState state, BlockItemUseContext useContext) {
      int i = state.get(LAYERS);
      if (useContext.getItem().getItem() == this.asItem() && i < 8) {
         if (useContext.replacingClickedOnBlock()) {
            return useContext.getFace() == EnumFacing.UP;
         } else {
            return true;
         }
      } else {
         return i == 1;
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = context.getWorld().getBlockState(context.getPos());
      if (iblockstate.getBlock() == this) {
         int i = iblockstate.get(LAYERS);
         return iblockstate.with(LAYERS, Integer.valueOf(Math.min(8, i + 1)));
      } else {
         return super.getStateForPlacement(context);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(LAYERS);
   }

   protected boolean canSilkHarvest() {
      return true;
   }
}
