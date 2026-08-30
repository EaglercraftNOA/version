package net.minecraft.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldSavedData;

public class VillageCollection extends WorldSavedData {
   private World world;
   private final List<BlockPos> villagerPositionsList = Lists.newArrayList();
   private final List<VillageDoorInfo> newDoors = Lists.newArrayList();
   private final List<Village> villageList = Lists.newArrayList();
   private int ticks;

   public VillageCollection(String name) {
      super(name);
   }

   public VillageCollection(World worldIn) {
      super(fileNameForProvider(worldIn.dimension));
      this.world = worldIn;
      this.markDirty();
   }

   public void setWorld(World worldIn) {
      this.world = worldIn;

      for(Village village : this.villageList) {
         village.setWorld(worldIn);
      }

   }

   public void addToVillagerPositionList(BlockPos pos) {
      if (this.villagerPositionsList.size() <= 64) {
         if (!this.positionInList(pos)) {
            this.villagerPositionsList.add(pos);
         }

      }
   }

   public void tick() {
      ++this.ticks;

      for(Village village : this.villageList) {
         village.tick(this.ticks);
      }

      this.removeAnnihilatedVillages();
      this.dropOldestVillagerPosition();
      this.addNewDoorsToVillageOrCreateVillage();
      if (this.ticks % 400 == 0) {
         this.markDirty();
      }

   }

   private void removeAnnihilatedVillages() {
      Iterator<Village> iterator = this.villageList.iterator();

      while(iterator.hasNext()) {
         Village village = iterator.next();
         if (village.isAnnihilated()) {
            iterator.remove();
            this.markDirty();
         }
      }

   }

   public List<Village> getVillageList() {
      return this.villageList;
   }

   public Village getNearestVillage(BlockPos doorBlock, int radius) {
      Village village = null;
      double d0 = (double)Float.MAX_VALUE;

      for(Village village1 : this.villageList) {
         double d1 = village1.getCenter().distanceSq(doorBlock);
         if (!(d1 >= d0)) {
            float f = (float)(radius + village1.getVillageRadius());
            if (!(d1 > (double)(f * f))) {
               village = village1;
               d0 = d1;
            }
         }
      }

      return village;
   }

   private void dropOldestVillagerPosition() {
      if (!this.villagerPositionsList.isEmpty()) {
         this.addDoorsAround(this.villagerPositionsList.remove(0));
      }
   }

   private void addNewDoorsToVillageOrCreateVillage() {
      for(int i = 0; i < this.newDoors.size(); ++i) {
         VillageDoorInfo villagedoorinfo = this.newDoors.get(i);
         Village village = this.getNearestVillage(villagedoorinfo.getDoorBlockPos(), 32);
         if (village == null) {
            village = new Village(this.world);
            this.villageList.add(village);
            this.markDirty();
         }

         village.addVillageDoorInfo(villagedoorinfo);
      }

      this.newDoors.clear();
   }

   private void addDoorsAround(BlockPos central) {
      int i = 16;
      int j = 4;
      int k = 16;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int l = -16; l < 16; ++l) {
         for(int i1 = -4; i1 < 4; ++i1) {
            for(int j1 = -16; j1 < 16; ++j1) {
               blockpos$mutableblockpos.setPos(central).move(l, i1, j1);
               IBlockState iblockstate = this.world.getBlockState(blockpos$mutableblockpos);
               if (this.isValidDoor(iblockstate)) {
                  VillageDoorInfo villagedoorinfo = this.checkDoorExistence(blockpos$mutableblockpos);
                  if (villagedoorinfo == null) {
                     this.addDoor(iblockstate, blockpos$mutableblockpos);
                  } else {
                     villagedoorinfo.setLastActivityTimestamp(this.ticks);
                  }
               }
            }
         }
      }

   }

   @Nullable
   private VillageDoorInfo checkDoorExistence(BlockPos doorBlock) {
      for(VillageDoorInfo villagedoorinfo : this.newDoors) {
         if (villagedoorinfo.getDoorBlockPos().getX() == doorBlock.getX() && villagedoorinfo.getDoorBlockPos().getZ() == doorBlock.getZ() && Math.abs(villagedoorinfo.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
            return villagedoorinfo;
         }
      }

      for(Village village : this.villageList) {
         VillageDoorInfo villagedoorinfo1 = village.getExistedDoor(doorBlock);
         if (villagedoorinfo1 != null) {
            return villagedoorinfo1;
         }
      }

      return null;
   }

   private void addDoor(IBlockState p_195927_1_, BlockPos p_195927_2_) {
      EnumFacing enumfacing = p_195927_1_.get(BlockDoor.FACING);
      EnumFacing enumfacing1 = enumfacing.getOpposite();
      int i = this.countBlocksCanSeeSky(p_195927_2_, enumfacing, 5);
      int j = this.countBlocksCanSeeSky(p_195927_2_, enumfacing1, i + 1);
      if (i != j) {
         this.newDoors.add(new VillageDoorInfo(p_195927_2_, i < j ? enumfacing : enumfacing1, this.ticks));
      }

   }

   private int countBlocksCanSeeSky(BlockPos centerPos, EnumFacing direction, int limitation) {
      int i = 0;

      for(int j = 1; j <= 5; ++j) {
         if (this.world.canSeeSky(centerPos.offset(direction, j))) {
            ++i;
            if (i >= limitation) {
               return i;
            }
         }
      }

      return i;
   }

   private boolean positionInList(BlockPos pos) {
      for(BlockPos blockpos : this.villagerPositionsList) {
         if (blockpos.equals(pos)) {
            return true;
         }
      }

      return false;
   }

   private boolean isValidDoor(IBlockState p_195928_1_) {
      return p_195928_1_.getBlock() instanceof BlockDoor && p_195928_1_.getMaterial() == Material.WOOD;
   }

   public void read(NBTTagCompound nbt) {
      this.ticks = nbt.getInt("Tick");
      NBTTagList nbttaglist = nbt.getList("Villages", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
         Village village = new Village();
         village.read(nbttagcompound);
         this.villageList.add(village);
      }

   }

   public NBTTagCompound write(NBTTagCompound compound) {
      compound.putInt("Tick", this.ticks);
      NBTTagList nbttaglist = new NBTTagList();

      for(Village village : this.villageList) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         village.write(nbttagcompound);
         nbttaglist.add((INBTBase)nbttagcompound);
      }

      compound.put("Villages", nbttaglist);
      return compound;
   }

   public static String fileNameForProvider(Dimension provider) {
      return "villages" + provider.getType().getSuffix();
   }
}
