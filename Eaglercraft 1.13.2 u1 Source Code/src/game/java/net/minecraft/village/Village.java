package net.minecraft.village;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathType;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class Village {
   private World world;
   private final List<VillageDoorInfo> villageDoorInfoList = Lists.newArrayList();
   private BlockPos centerHelper = BlockPos.ORIGIN;
   private BlockPos center = BlockPos.ORIGIN;
   private int villageRadius;
   private int lastAddDoorTimestamp;
   private int tickCounter;
   private int villagerCount;
   private int noBreedTicks;
   private final Map<String, Integer> playerReputation = Maps.newHashMap();
   private final List<Village.VillageAggressor> villageAgressors = Lists.newArrayList();
   private int golemCount;

   public Village() {
   }

   public Village(World worldIn) {
      this.world = worldIn;
   }

   public void setWorld(World worldIn) {
      this.world = worldIn;
   }

   public void tick(int tickCounterIn) {
      this.tickCounter = tickCounterIn;
      this.removeDeadAndOutOfRangeDoors();
      this.removeDeadAndOldAgressors();
      if (tickCounterIn % 20 == 0) {
         this.updateVillagerCount();
      }

      if (tickCounterIn % 30 == 0) {
         this.updateGolemCount();
      }

      int i = this.villagerCount / 10;
      if (this.golemCount < i && this.villageDoorInfoList.size() > 20 && this.world.rand.nextInt(7000) == 0) {
         Entity entity = this.spawnIronGolem(this.center);
         if (entity != null) {
            ++this.golemCount;
         }
      }

   }

   @Nullable
   private Entity spawnIronGolem(BlockPos p_208059_1_) {
      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos = p_208059_1_.add(this.world.rand.nextInt(16) - 8, this.world.rand.nextInt(6) - 3, this.world.rand.nextInt(16) - 8);
         if (this.isBlockPosWithinSqVillageRadius(blockpos)) {
            EntityIronGolem entityirongolem = EntityType.IRON_GOLEM.create(this.world, (NBTTagCompound)null, (ITextComponent)null, (EntityPlayer)null, blockpos, false, false);
            if (entityirongolem != null) {
               if (entityirongolem.canSpawn(this.world, false) && entityirongolem.isNotColliding(this.world)) {
                  this.world.spawnEntity(entityirongolem);
                  return entityirongolem;
               }

               entityirongolem.remove();
            }
         }
      }

      return null;
   }

   private void updateGolemCount() {
      List<EntityIronGolem> list = this.world.getEntitiesWithinAABB(EntityIronGolem.class, new AxisAlignedBB((double)(this.center.getX() - this.villageRadius), (double)(this.center.getY() - 4), (double)(this.center.getZ() - this.villageRadius), (double)(this.center.getX() + this.villageRadius), (double)(this.center.getY() + 4), (double)(this.center.getZ() + this.villageRadius)));
      this.golemCount = list.size();
   }

   private void updateVillagerCount() {
      List<EntityVillager> list = this.world.getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB((double)(this.center.getX() - this.villageRadius), (double)(this.center.getY() - 4), (double)(this.center.getZ() - this.villageRadius), (double)(this.center.getX() + this.villageRadius), (double)(this.center.getY() + 4), (double)(this.center.getZ() + this.villageRadius)));
      this.villagerCount = list.size();
      if (this.villagerCount == 0) {
         this.playerReputation.clear();
      }

   }

   public BlockPos getCenter() {
      return this.center;
   }

   public int getVillageRadius() {
      return this.villageRadius;
   }

   public int getNumVillageDoors() {
      return this.villageDoorInfoList.size();
   }

   public int getTicksSinceLastDoorAdding() {
      return this.tickCounter - this.lastAddDoorTimestamp;
   }

   public int getNumVillagers() {
      return this.villagerCount;
   }

   public boolean isBlockPosWithinSqVillageRadius(BlockPos pos) {
      return this.center.distanceSq(pos) < (double)(this.villageRadius * this.villageRadius);
   }

   public List<VillageDoorInfo> getVillageDoorInfoList() {
      return this.villageDoorInfoList;
   }

   public VillageDoorInfo getNearestDoor(BlockPos pos) {
      VillageDoorInfo villagedoorinfo = null;
      int i = Integer.MAX_VALUE;

      for(VillageDoorInfo villagedoorinfo1 : this.villageDoorInfoList) {
         int j = villagedoorinfo1.getDistanceToDoorBlockSq(pos);
         if (j < i) {
            villagedoorinfo = villagedoorinfo1;
            i = j;
         }
      }

      return villagedoorinfo;
   }

   public VillageDoorInfo getDoorInfo(BlockPos pos) {
      VillageDoorInfo villagedoorinfo = null;
      int i = Integer.MAX_VALUE;

      for(VillageDoorInfo villagedoorinfo1 : this.villageDoorInfoList) {
         int j = villagedoorinfo1.getDistanceToDoorBlockSq(pos);
         if (j > 256) {
            j = j * 1000;
         } else {
            j = villagedoorinfo1.getDoorOpeningRestrictionCounter();
         }

         if (j < i) {
            BlockPos blockpos = villagedoorinfo1.getDoorBlockPos();
            EnumFacing enumfacing = villagedoorinfo1.getInsideDirection();
            if (this.world.getBlockState(blockpos.offset(enumfacing, 1)).allowsMovement(this.world, blockpos.offset(enumfacing, 1), PathType.LAND) && this.world.getBlockState(blockpos.offset(enumfacing, -1)).allowsMovement(this.world, blockpos.offset(enumfacing, -1), PathType.LAND) && this.world.getBlockState(blockpos.up().offset(enumfacing, 1)).allowsMovement(this.world, blockpos.up().offset(enumfacing, 1), PathType.LAND) && this.world.getBlockState(blockpos.up().offset(enumfacing, -1)).allowsMovement(this.world, blockpos.up().offset(enumfacing, -1), PathType.LAND)) {
               villagedoorinfo = villagedoorinfo1;
               i = j;
            }
         }
      }

      return villagedoorinfo;
   }

   @Nullable
   public VillageDoorInfo getExistedDoor(BlockPos doorBlock) {
      if (this.center.distanceSq(doorBlock) > (double)(this.villageRadius * this.villageRadius)) {
         return null;
      } else {
         for(VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
            if (villagedoorinfo.getDoorBlockPos().getX() == doorBlock.getX() && villagedoorinfo.getDoorBlockPos().getZ() == doorBlock.getZ() && Math.abs(villagedoorinfo.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
               return villagedoorinfo;
            }
         }

         return null;
      }
   }

   public void addVillageDoorInfo(VillageDoorInfo doorInfo) {
      this.villageDoorInfoList.add(doorInfo);
      this.centerHelper = this.centerHelper.add(doorInfo.getDoorBlockPos());
      this.updateVillageRadiusAndCenter();
      this.lastAddDoorTimestamp = doorInfo.getLastActivityTimestamp();
   }

   public boolean isAnnihilated() {
      return this.villageDoorInfoList.isEmpty();
   }

   public void addOrRenewAgressor(EntityLivingBase entitylivingbaseIn) {
      for(Village.VillageAggressor village$villageaggressor : this.villageAgressors) {
         if (village$villageaggressor.agressor == entitylivingbaseIn) {
            village$villageaggressor.agressionTime = this.tickCounter;
            return;
         }
      }

      this.villageAgressors.add(new Village.VillageAggressor(entitylivingbaseIn, this.tickCounter));
   }

   @Nullable
   public EntityLivingBase findNearestVillageAggressor(EntityLivingBase entitylivingbaseIn) {
      double d0 = Double.MAX_VALUE;
      Village.VillageAggressor village$villageaggressor = null;

      for(int i = 0; i < this.villageAgressors.size(); ++i) {
         Village.VillageAggressor village$villageaggressor1 = this.villageAgressors.get(i);
         double d1 = village$villageaggressor1.agressor.getDistanceSq(entitylivingbaseIn);
         if (!(d1 > d0)) {
            village$villageaggressor = village$villageaggressor1;
            d0 = d1;
         }
      }

      return village$villageaggressor == null ? null : village$villageaggressor.agressor;
   }

   public EntityPlayer getNearestTargetPlayer(EntityLivingBase villageDefender) {
      double d0 = Double.MAX_VALUE;
      EntityPlayer entityplayer = null;

      for(String s : this.playerReputation.keySet()) {
         if (this.isPlayerReputationTooLow(s)) {
            EntityPlayer entityplayer1 = this.world.getPlayerEntityByName(s);
            if (entityplayer1 != null) {
               double d1 = entityplayer1.getDistanceSq(villageDefender);
               if (!(d1 > d0)) {
                  entityplayer = entityplayer1;
                  d0 = d1;
               }
            }
         }
      }

      return entityplayer;
   }

   private void removeDeadAndOldAgressors() {
      Iterator<Village.VillageAggressor> iterator = this.villageAgressors.iterator();

      while(iterator.hasNext()) {
         Village.VillageAggressor village$villageaggressor = iterator.next();
         if (!village$villageaggressor.agressor.isAlive() || Math.abs(this.tickCounter - village$villageaggressor.agressionTime) > 300) {
            iterator.remove();
         }
      }

   }

   private void removeDeadAndOutOfRangeDoors() {
      boolean flag = false;
      boolean flag1 = this.world.rand.nextInt(50) == 0;
      Iterator<VillageDoorInfo> iterator = this.villageDoorInfoList.iterator();

      while(iterator.hasNext()) {
         VillageDoorInfo villagedoorinfo = iterator.next();
         if (flag1) {
            villagedoorinfo.resetDoorOpeningRestrictionCounter();
         }

         if (!this.isWoodDoor(villagedoorinfo.getDoorBlockPos()) || Math.abs(this.tickCounter - villagedoorinfo.getLastActivityTimestamp()) > 1200) {
            this.centerHelper = this.centerHelper.subtract(villagedoorinfo.getDoorBlockPos());
            flag = true;
            villagedoorinfo.setIsDetachedFromVillageFlag(true);
            iterator.remove();
         }
      }

      if (flag) {
         this.updateVillageRadiusAndCenter();
      }

   }

   private boolean isWoodDoor(BlockPos pos) {
      IBlockState iblockstate = this.world.getBlockState(pos);
      Block block = iblockstate.getBlock();
      if (block instanceof BlockDoor) {
         return iblockstate.getMaterial() == Material.WOOD;
      } else {
         return false;
      }
   }

   private void updateVillageRadiusAndCenter() {
      int i = this.villageDoorInfoList.size();
      if (i == 0) {
         this.center = BlockPos.ORIGIN;
         this.villageRadius = 0;
      } else {
         this.center = new BlockPos(this.centerHelper.getX() / i, this.centerHelper.getY() / i, this.centerHelper.getZ() / i);
         int j = 0;

         for(VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
            j = Math.max(villagedoorinfo.getDistanceToDoorBlockSq(this.center), j);
         }

         this.villageRadius = Math.max(32, (int)Math.sqrt((double)j) + 1);
      }
   }

   public int getPlayerReputation(String playerName) {
      Integer integer = this.playerReputation.get(playerName);
      return integer == null ? 0 : integer;
   }

   public int modifyPlayerReputation(String playerName, int reputation) {
      int i = this.getPlayerReputation(playerName);
      int j = MathHelper.clamp(i + reputation, -30, 10);
      this.playerReputation.put(playerName, j);
      return j;
   }

   public boolean isPlayerReputationTooLow(String playerName) {
      return this.getPlayerReputation(playerName) <= -15;
   }

   public void read(NBTTagCompound compound) {
      this.villagerCount = compound.getInt("PopSize");
      this.villageRadius = compound.getInt("Radius");
      this.golemCount = compound.getInt("Golems");
      this.lastAddDoorTimestamp = compound.getInt("Stable");
      this.tickCounter = compound.getInt("Tick");
      this.noBreedTicks = compound.getInt("MTick");
      this.center = new BlockPos(compound.getInt("CX"), compound.getInt("CY"), compound.getInt("CZ"));
      this.centerHelper = new BlockPos(compound.getInt("ACX"), compound.getInt("ACY"), compound.getInt("ACZ"));
      NBTTagList nbttaglist = compound.getList("Doors", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
         VillageDoorInfo villagedoorinfo = new VillageDoorInfo(new BlockPos(nbttagcompound.getInt("X"), nbttagcompound.getInt("Y"), nbttagcompound.getInt("Z")), nbttagcompound.getInt("IDX"), nbttagcompound.getInt("IDZ"), nbttagcompound.getInt("TS"));
         this.villageDoorInfoList.add(villagedoorinfo);
      }

      NBTTagList nbttaglist1 = compound.getList("Players", 10);

      for(int j = 0; j < nbttaglist1.size(); ++j) {
         NBTTagCompound nbttagcompound1 = nbttaglist1.getCompound(j);
         if (nbttagcompound1.contains("UUID") && this.world != null && this.world.getServer() != null) {
            PlayerProfileCache playerprofilecache = this.world.getServer().getPlayerProfileCache();
            GameProfile gameprofile = playerprofilecache.getProfileByUUID(UUID.fromString(nbttagcompound1.getString("UUID")));
            if (gameprofile != null) {
               this.playerReputation.put(gameprofile.getName(), nbttagcompound1.getInt("S"));
            }
         } else {
            this.playerReputation.put(nbttagcompound1.getString("Name"), nbttagcompound1.getInt("S"));
         }
      }

   }

   public void write(NBTTagCompound compound) {
      compound.putInt("PopSize", this.villagerCount);
      compound.putInt("Radius", this.villageRadius);
      compound.putInt("Golems", this.golemCount);
      compound.putInt("Stable", this.lastAddDoorTimestamp);
      compound.putInt("Tick", this.tickCounter);
      compound.putInt("MTick", this.noBreedTicks);
      compound.putInt("CX", this.center.getX());
      compound.putInt("CY", this.center.getY());
      compound.putInt("CZ", this.center.getZ());
      compound.putInt("ACX", this.centerHelper.getX());
      compound.putInt("ACY", this.centerHelper.getY());
      compound.putInt("ACZ", this.centerHelper.getZ());
      NBTTagList nbttaglist = new NBTTagList();

      for(VillageDoorInfo villagedoorinfo : this.villageDoorInfoList) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.putInt("X", villagedoorinfo.getDoorBlockPos().getX());
         nbttagcompound.putInt("Y", villagedoorinfo.getDoorBlockPos().getY());
         nbttagcompound.putInt("Z", villagedoorinfo.getDoorBlockPos().getZ());
         nbttagcompound.putInt("IDX", villagedoorinfo.getInsideOffsetX());
         nbttagcompound.putInt("IDZ", villagedoorinfo.getInsideOffsetZ());
         nbttagcompound.putInt("TS", villagedoorinfo.getLastActivityTimestamp());
         nbttaglist.add((INBTBase)nbttagcompound);
      }

      compound.put("Doors", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(String s : this.playerReputation.keySet()) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         PlayerProfileCache playerprofilecache = this.world.getServer().getPlayerProfileCache();

         try {
            GameProfile gameprofile = playerprofilecache.getGameProfileForUsername(s);
            if (gameprofile != null) {
               nbttagcompound1.putString("UUID", gameprofile.getId().toString());
               nbttagcompound1.putInt("S", this.playerReputation.get(s));
               nbttaglist1.add((INBTBase)nbttagcompound1);
            }
         } catch (RuntimeException var9) {
            ;
         }
      }

      compound.put("Players", nbttaglist1);
   }

   public void endMatingSeason() {
      this.noBreedTicks = this.tickCounter;
   }

   public boolean isMatingSeason() {
      return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
   }

   public void setDefaultPlayerReputation(int defaultReputation) {
      for(String s : this.playerReputation.keySet()) {
         this.modifyPlayerReputation(s, defaultReputation);
      }

   }

   class VillageAggressor {
      public EntityLivingBase agressor;
      public int agressionTime;

      VillageAggressor(EntityLivingBase agressorIn, int agressionTimeIn) {
         this.agressor = agressorIn;
         this.agressionTime = agressionTimeIn;
      }
   }
}
