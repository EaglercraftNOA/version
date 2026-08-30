package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockCoralWallFanDead;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemBoneMeal extends ItemDye {
   public ItemBoneMeal(EnumDyeColor dyeColor, Item.Properties builder) {
      super(dyeColor, builder);
   }

   public EnumActionResult onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      BlockPos blockpos1 = blockpos.offset(context.getFace());
      if (applyBonemeal(context.getItem(), world, blockpos)) {
         if (!world.isRemote) {
            world.playEvent(2005, blockpos, 0);
         }

         return EnumActionResult.SUCCESS;
      } else {
         IBlockState iblockstate = world.getBlockState(blockpos);
         boolean flag = iblockstate.getBlockFaceShape(world, blockpos, context.getFace()) == BlockFaceShape.SOLID;
         if (flag && growSeagrass(context.getItem(), world, blockpos1, context.getFace())) {
            if (!world.isRemote) {
               world.playEvent(2005, blockpos1, 0);
            }

            return EnumActionResult.SUCCESS;
         } else {
            return EnumActionResult.PASS;
         }
      }
   }

   public static boolean applyBonemeal(ItemStack p_195966_0_, World p_195966_1_, BlockPos p_195966_2_) {
      IBlockState iblockstate = p_195966_1_.getBlockState(p_195966_2_);
      if (iblockstate.getBlock() instanceof IGrowable) {
         IGrowable igrowable = (IGrowable)iblockstate.getBlock();
         if (igrowable.canGrow(p_195966_1_, p_195966_2_, iblockstate, p_195966_1_.isRemote)) {
            if (!p_195966_1_.isRemote) {
               if (igrowable.canUseBonemeal(p_195966_1_, p_195966_1_.rand, p_195966_2_, iblockstate)) {
                  igrowable.grow(p_195966_1_, p_195966_1_.rand, p_195966_2_, iblockstate);
               }

               p_195966_0_.shrink(1);
            }

            return true;
         }
      }

      return false;
   }

   public static boolean growSeagrass(ItemStack p_203173_0_, World p_203173_1_, BlockPos p_203173_2_, @Nullable EnumFacing p_203173_3_) {
      if (p_203173_1_.getBlockState(p_203173_2_).getBlock() == Blocks.WATER && p_203173_1_.getFluidState(p_203173_2_).getLevel() == 8) {
         if (!p_203173_1_.isRemote) {
            label79:
            for(int i = 0; i < 128; ++i) {
               BlockPos blockpos = p_203173_2_;
               Biome biome = p_203173_1_.getBiome(p_203173_2_);
               IBlockState iblockstate = Blocks.SEAGRASS.getDefaultState();

               for(int j = 0; j < i / 16; ++j) {
                  blockpos = blockpos.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                  biome = p_203173_1_.getBiome(blockpos);
                  if (p_203173_1_.getBlockState(blockpos).isBlockNormalCube()) {
                     continue label79;
                  }
               }

               if (biome == Biomes.WARM_OCEAN || biome == Biomes.DEEP_WARM_OCEAN) {
                  if (i == 0 && p_203173_3_ != null && p_203173_3_.getAxis().isHorizontal()) {
                     iblockstate = BlockTags.WALL_CORALS.getRandomElement(p_203173_1_.rand).getDefaultState().with(BlockCoralWallFanDead.FACING, p_203173_3_);
                  } else if (random.nextInt(4) == 0) {
                     iblockstate = BlockTags.UNDERWATER_BONEMEALS.getRandomElement(random).getDefaultState();
                  }
               }

               if (iblockstate.getBlock().isIn(BlockTags.WALL_CORALS)) {
                  for(int k = 0; !iblockstate.isValidPosition(p_203173_1_, blockpos) && k < 4; ++k) {
                     iblockstate = iblockstate.with(BlockCoralWallFanDead.FACING, EnumFacing.Plane.HORIZONTAL.random(random));
                  }
               }

               if (iblockstate.isValidPosition(p_203173_1_, blockpos)) {
                  IBlockState iblockstate1 = p_203173_1_.getBlockState(blockpos);
                  if (iblockstate1.getBlock() == Blocks.WATER && p_203173_1_.getFluidState(blockpos).getLevel() == 8) {
                     p_203173_1_.setBlockState(blockpos, iblockstate, 3);
                  } else if (iblockstate1.getBlock() == Blocks.SEAGRASS && random.nextInt(10) == 0) {
                     ((IGrowable)Blocks.SEAGRASS).grow(p_203173_1_, random, blockpos, iblockstate1);
                  }
               }
            }

            p_203173_0_.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnBonemealParticles(IWorld worldIn, BlockPos posIn, int data) {
      if (data == 0) {
         data = 15;
      }

      IBlockState iblockstate = worldIn.getBlockState(posIn);
      if (!iblockstate.isAir()) {
         for(int i = 0; i < data; ++i) {
            double d0 = random.nextGaussian() * 0.02D;
            double d1 = random.nextGaussian() * 0.02D;
            double d2 = random.nextGaussian() * 0.02D;
            worldIn.addParticle(Particles.HAPPY_VILLAGER, (double)((float)posIn.getX() + random.nextFloat()), (double)posIn.getY() + (double)random.nextFloat() * iblockstate.getShape(worldIn, posIn).getEnd(EnumFacing.Axis.Y), (double)((float)posIn.getZ() + random.nextFloat()), d0, d1, d2);
         }

      }
   }
}
