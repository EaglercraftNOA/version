package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockEndPortal extends BlockContainer {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

   protected BlockEndPortal(Block.Properties builder) {
      super(builder);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityEndPortal();
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (!worldIn.isRemote && !entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss() && VoxelShapes.compare(VoxelShapes.create(entityIn.getBoundingBox().offset((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()))), state.getShape(worldIn, pos), IBooleanFunction.AND)) {
         entityIn.changeDimension(DimensionType.THE_END);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      double d0 = (double)((float)pos.getX() + rand.nextFloat());
      double d1 = (double)((float)pos.getY() + 0.8F);
      double d2 = (double)((float)pos.getZ() + rand.nextFloat());
      double d3 = 0.0D;
      double d4 = 0.0D;
      double d5 = 0.0D;
      worldIn.addParticle(Particles.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return ItemStack.EMPTY;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
