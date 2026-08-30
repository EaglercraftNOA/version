package net.minecraft.command.arguments;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class BlockStateInput implements Predicate<BlockWorldState> {
   private final IBlockState state;
   private final Set<IProperty<?>> properties;
   @Nullable
   private final NBTTagCompound tag;

   public BlockStateInput(IBlockState stateIn, Set<IProperty<?>> propertiesIn, @Nullable NBTTagCompound nbtIn) {
      this.state = stateIn;
      this.properties = propertiesIn;
      this.tag = nbtIn;
   }

   public IBlockState getState() {
      return this.state;
   }

   public boolean test(BlockWorldState p_test_1_) {
      IBlockState iblockstate = p_test_1_.getBlockState();
      if (iblockstate.getBlock() != this.state.getBlock()) {
         return false;
      } else {
         for(IProperty<?> iproperty : this.properties) {
            if (iblockstate.get(iproperty) != this.state.get(iproperty)) {
               return false;
            }
         }

         if (this.tag == null) {
            return true;
         } else {
            TileEntity tileentity = p_test_1_.getTileEntity();
            return tileentity != null && NBTUtil.areNBTEquals(this.tag, tileentity.write(new NBTTagCompound()), true);
         }
      }
   }

   public boolean place(WorldServer worldIn, BlockPos pos, int flags) {
      if (!worldIn.setBlockState(pos, this.state, flags)) {
         return false;
      } else {
         if (this.tag != null) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity != null) {
               NBTTagCompound nbttagcompound = this.tag.copy();
               nbttagcompound.putInt("x", pos.getX());
               nbttagcompound.putInt("y", pos.getY());
               nbttagcompound.putInt("z", pos.getZ());
               tileentity.read(nbttagcompound);
            }
         }

         return true;
      }
   }
}
