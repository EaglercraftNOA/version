package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class BlockAbstractSkull extends BlockContainer {
   private final BlockSkull.ISkullType skullType;

   public BlockAbstractSkull(BlockSkull.ISkullType p_i48452_1_, Block.Properties properties) {
      super(properties);
      this.skullType = p_i48452_1_;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState state) {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntitySkull();
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
   }

   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (!worldIn.isRemote && player.abilities.isCreativeMode) {
         TileEntitySkull.disableDrop(worldIn, pos);
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock() && !worldIn.isRemote) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntitySkull) {
            TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
            if (tileentityskull.shouldDrop()) {
               ItemStack itemstack = this.getItem(worldIn, pos, state);
               Block block = tileentityskull.getBlockState().getBlock();
               if ((block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) && tileentityskull.getPlayerProfile() != null) {
                  NBTTagCompound nbttagcompound = new NBTTagCompound();
                  NBTUtil.writeGameProfile(nbttagcompound, tileentityskull.getPlayerProfile());
                  itemstack.getOrCreateTag().put("SkullOwner", nbttagcompound);
               }

               spawnAsEntity(worldIn, pos, itemstack);
            }
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public BlockSkull.ISkullType getSkullType() {
      return this.skullType;
   }
}
