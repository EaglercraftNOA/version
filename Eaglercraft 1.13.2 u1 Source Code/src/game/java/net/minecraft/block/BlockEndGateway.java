package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockEndGateway extends BlockContainer {
   protected BlockEndGateway(Block.Properties builder) {
      super(builder);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityEndGateway();
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityEndGateway) {
         int i = ((TileEntityEndGateway)tileentity).getParticleAmount();

         for(int j = 0; j < i; ++j) {
            double d0 = (double)((float)pos.getX() + rand.nextFloat());
            double d1 = (double)((float)pos.getY() + rand.nextFloat());
            double d2 = (double)((float)pos.getZ() + rand.nextFloat());
            double d3 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double d4 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double d5 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            int k = rand.nextInt(2) * 2 - 1;
            if (rand.nextBoolean()) {
               d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)k;
               d5 = (double)(rand.nextFloat() * 2.0F * (float)k);
            } else {
               d0 = (double)pos.getX() + 0.5D + 0.25D * (double)k;
               d3 = (double)(rand.nextFloat() * 2.0F * (float)k);
            }

            worldIn.addParticle(Particles.PORTAL, d0, d1, d2, d3, d4, d5);
         }

      }
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return ItemStack.EMPTY;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
