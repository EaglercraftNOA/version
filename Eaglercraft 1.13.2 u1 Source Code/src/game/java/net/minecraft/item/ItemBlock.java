package net.minecraft.item;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemBlock extends Item {
   @Deprecated
   private final Block block;

   public ItemBlock(Block blockIn, Item.Properties builder) {
      super(builder);
      this.block = blockIn;
   }

   public EnumActionResult onItemUse(ItemUseContext context) {
      return this.tryPlace(new BlockItemUseContext(context));
   }

   public EnumActionResult tryPlace(BlockItemUseContext p_195942_1_) {
      if (!p_195942_1_.canPlace()) {
         return EnumActionResult.FAIL;
      } else {
         IBlockState iblockstate = this.getStateForPlacement(p_195942_1_);
         if (iblockstate == null) {
            return EnumActionResult.FAIL;
         } else if (!this.placeBlock(p_195942_1_, iblockstate)) {
            return EnumActionResult.FAIL;
         } else {
            BlockPos blockpos = p_195942_1_.getPos();
            World world = p_195942_1_.getWorld();
            EntityPlayer entityplayer = p_195942_1_.getPlayer();
            ItemStack itemstack = p_195942_1_.getItem();
            IBlockState iblockstate1 = world.getBlockState(blockpos);
            Block block = iblockstate1.getBlock();
            if (block == iblockstate.getBlock()) {
               this.onBlockPlaced(blockpos, world, entityplayer, itemstack, iblockstate1);
               block.onBlockPlacedBy(world, blockpos, iblockstate1, entityplayer, itemstack);
               if (entityplayer instanceof EntityPlayerMP) {
                  CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, blockpos, itemstack);
               }
            }

            SoundType soundtype = block.getSoundType();
            world.playSound(entityplayer, blockpos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            itemstack.shrink(1);
            return EnumActionResult.SUCCESS;
         }
      }
   }

   protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable EntityPlayer player, ItemStack stack, IBlockState state) {
      return setTileEntityNBT(worldIn, player, pos, stack);
   }

   @Nullable
   protected IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = this.getBlock().getStateForPlacement(context);
      return iblockstate != null && this.canPlace(context, iblockstate) ? iblockstate : null;
   }

   protected boolean canPlace(BlockItemUseContext p_195944_1_, IBlockState p_195944_2_) {
      return p_195944_2_.isValidPosition(p_195944_1_.getWorld(), p_195944_1_.getPos()) && p_195944_1_.getWorld().checkNoEntityCollision(p_195944_2_, p_195944_1_.getPos());
   }

   protected boolean placeBlock(BlockItemUseContext context, IBlockState state) {
      return context.getWorld().setBlockState(context.getPos(), state, 11);
   }

   public static boolean setTileEntityNBT(World worldIn, @Nullable EntityPlayer player, BlockPos pos, ItemStack stackIn) {
      MinecraftServer minecraftserver = worldIn.getServer();
      if (minecraftserver == null) {
         return false;
      } else {
         NBTTagCompound nbttagcompound = stackIn.getChildTag("BlockEntityTag");
         if (nbttagcompound != null) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity != null) {
               if (!worldIn.isRemote && tileentity.onlyOpsCanSetNbt() && (player == null || !player.canUseCommandBlock())) {
                  return false;
               }

               NBTTagCompound nbttagcompound1 = tileentity.write(new NBTTagCompound());
               NBTTagCompound nbttagcompound2 = nbttagcompound1.copy();
               nbttagcompound1.merge(nbttagcompound);
               nbttagcompound1.putInt("x", pos.getX());
               nbttagcompound1.putInt("y", pos.getY());
               nbttagcompound1.putInt("z", pos.getZ());
               if (!nbttagcompound1.equals(nbttagcompound2)) {
                  tileentity.read(nbttagcompound1);
                  tileentity.markDirty();
                  return true;
               }
            }
         }

         return false;
      }
   }

   public String getTranslationKey() {
      return this.getBlock().getTranslationKey();
   }

   public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
      if (this.isInGroup(group)) {
         this.getBlock().fillItemGroup(group, items);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      this.getBlock().addInformation(stack, worldIn, tooltip, flagIn);
   }

   public Block getBlock() {
      return this.block;
   }

   public void addToBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
      blockToItemMap.put(this.getBlock(), itemIn);
   }
}
