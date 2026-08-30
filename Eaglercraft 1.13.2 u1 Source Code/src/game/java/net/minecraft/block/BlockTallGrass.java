package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockTallGrass extends BlockBush implements IGrowable {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   protected BlockTallGrass(Block.Properties properties) {
      super(properties);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return worldIn.rand.nextInt(8) == 0 ? Items.WHEAT_SEEDS : Items.AIR;
   }

   public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
      return 1 + random.nextInt(fortune * 2 + 1);
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
         player.addStat(StatList.BLOCK_MINED.get(this));
         player.addExhaustion(0.005F);
         spawnAsEntity(worldIn, pos, new ItemStack(this));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te, stack);
      }

   }

   public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      BlockDoublePlant blockdoubleplant = (BlockDoublePlant)(this == Blocks.FERN ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);
      if (blockdoubleplant.getDefaultState().isValidPosition(worldIn, pos) && worldIn.isAirBlock(pos.up())) {
         blockdoubleplant.placeAt(worldIn, pos, 2);
      }

   }

   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.XYZ;
   }
}
