package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class BlockBeacon extends BlockContainer {
   public BlockBeacon(Block.Properties properties) {
      super(properties);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityBeacon();
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityBeacon) {
            player.displayGUIChest((TileEntityBeacon)tileentity);
            player.addStat(StatList.BEACON_INTERACTION);
         }

         return true;
      }
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityBeacon) {
            ((TileEntityBeacon)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public static void updateColorAsync(World worldIn, BlockPos glassPos) {
      HttpUtil.DOWNLOADER_EXECUTOR.submit(() -> {
         Chunk chunk = worldIn.getChunk(glassPos);

         for(int i = glassPos.getY() - 1; i >= 0; --i) {
            BlockPos blockpos = new BlockPos(glassPos.getX(), i, glassPos.getZ());
            if (!chunk.canSeeSky(blockpos)) {
               break;
            }

            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            if (iblockstate.getBlock() == Blocks.BEACON) {
               ((WorldServer)worldIn).addScheduledTask(() -> {
                  TileEntity tileentity = worldIn.getTileEntity(blockpos);
                  if (tileentity instanceof TileEntityBeacon) {
                     ((TileEntityBeacon)tileentity).updateBeacon();
                     worldIn.addBlockEvent(blockpos, Blocks.BEACON, 1, 0);
                  }

               });
            }
         }

      });
   }
}
