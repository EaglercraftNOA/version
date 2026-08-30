package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

public class MapData extends WorldSavedData {
   public int xCenter;
   public int zCenter;
   public DimensionType dimension;
   public boolean trackingPosition;
   public boolean unlimitedTracking;
   public byte scale;
   public byte[] colors = new byte[16384];
   public List<MapData.MapInfo> playersArrayList = Lists.newArrayList();
   private final Map<EntityPlayer, MapData.MapInfo> playersHashMap = Maps.newHashMap();
   private final Map<String, MapBanner> banners = Maps.newHashMap();
   public Map<String, MapDecoration> mapDecorations = Maps.newLinkedHashMap();
   private final Map<String, MapFrame> frames = Maps.newHashMap();

   public MapData(String mapname) {
      super(mapname);
   }

   public void func_212440_a(int p_212440_1_, int p_212440_2_, int p_212440_3_, boolean p_212440_4_, boolean p_212440_5_, DimensionType p_212440_6_) {
      this.scale = (byte)p_212440_3_;
      this.calculateMapCenter((double)p_212440_1_, (double)p_212440_2_, this.scale);
      this.dimension = p_212440_6_;
      this.trackingPosition = p_212440_4_;
      this.unlimitedTracking = p_212440_5_;
      this.markDirty();
   }

   public void calculateMapCenter(double x, double z, int mapScale) {
      int i = 128 * (1 << mapScale);
      int j = MathHelper.floor((x + 64.0D) / (double)i);
      int k = MathHelper.floor((z + 64.0D) / (double)i);
      this.xCenter = j * i + i / 2 - 64;
      this.zCenter = k * i + i / 2 - 64;
   }

   public void read(NBTTagCompound nbt) {
      this.dimension = DimensionType.getById(nbt.getInt("dimension"));
      this.xCenter = nbt.getInt("xCenter");
      this.zCenter = nbt.getInt("zCenter");
      this.scale = (byte)MathHelper.clamp(nbt.getByte("scale"), 0, 4);
      this.trackingPosition = !nbt.contains("trackingPosition", 1) || nbt.getBoolean("trackingPosition");
      this.unlimitedTracking = nbt.getBoolean("unlimitedTracking");
      this.colors = nbt.getByteArray("colors");
      if (this.colors.length != 16384) {
         this.colors = new byte[16384];
      }

      NBTTagList nbttaglist = nbt.getList("banners", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         MapBanner mapbanner = MapBanner.read(nbttaglist.getCompound(i));
         this.banners.put(mapbanner.getMapDecorationId(), mapbanner);
         this.updateDecorations(mapbanner.getDecorationType(), (IWorld)null, mapbanner.getMapDecorationId(), (double)mapbanner.getPos().getX(), (double)mapbanner.getPos().getZ(), 180.0D, mapbanner.getName());
      }

      NBTTagList nbttaglist1 = nbt.getList("frames", 10);

      for(int j = 0; j < nbttaglist1.size(); ++j) {
         MapFrame mapframe = MapFrame.read(nbttaglist1.getCompound(j));
         this.frames.put(mapframe.func_212767_e(), mapframe);
         this.updateDecorations(MapDecoration.Type.FRAME, (IWorld)null, "frame-" + mapframe.getEntityId(), (double)mapframe.getPos().getX(), (double)mapframe.getPos().getZ(), (double)mapframe.getRotation(), (ITextComponent)null);
      }

   }

   public NBTTagCompound write(NBTTagCompound compound) {
      compound.putInt("dimension", this.dimension.getId());
      compound.putInt("xCenter", this.xCenter);
      compound.putInt("zCenter", this.zCenter);
      compound.putByte("scale", this.scale);
      compound.putByteArray("colors", this.colors);
      compound.putBoolean("trackingPosition", this.trackingPosition);
      compound.putBoolean("unlimitedTracking", this.unlimitedTracking);
      NBTTagList nbttaglist = new NBTTagList();

      for(MapBanner mapbanner : this.banners.values()) {
         nbttaglist.add((INBTBase)mapbanner.write());
      }

      compound.put("banners", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(MapFrame mapframe : this.frames.values()) {
         nbttaglist1.add((INBTBase)mapframe.write());
      }

      compound.put("frames", nbttaglist1);
      return compound;
   }

   public void updateVisiblePlayers(EntityPlayer player, ItemStack mapStack) {
      if (!this.playersHashMap.containsKey(player)) {
         MapData.MapInfo mapdata$mapinfo = new MapData.MapInfo(player);
         this.playersHashMap.put(player, mapdata$mapinfo);
         this.playersArrayList.add(mapdata$mapinfo);
      }

      if (!player.inventory.hasItemStack(mapStack)) {
         this.mapDecorations.remove(player.getName().getString());
      }

      for(int i = 0; i < this.playersArrayList.size(); ++i) {
         MapData.MapInfo mapdata$mapinfo1 = this.playersArrayList.get(i);
         String s = mapdata$mapinfo1.player.getName().getString();
         if (!mapdata$mapinfo1.player.removed && (mapdata$mapinfo1.player.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame())) {
            if (!mapStack.isOnItemFrame() && mapdata$mapinfo1.player.dimension == this.dimension && this.trackingPosition) {
               this.updateDecorations(MapDecoration.Type.PLAYER, mapdata$mapinfo1.player.world, s, mapdata$mapinfo1.player.posX, mapdata$mapinfo1.player.posZ, (double)mapdata$mapinfo1.player.rotationYaw, (ITextComponent)null);
            }
         } else {
            this.playersHashMap.remove(mapdata$mapinfo1.player);
            this.playersArrayList.remove(mapdata$mapinfo1);
            this.mapDecorations.remove(s);
         }
      }

      if (mapStack.isOnItemFrame() && this.trackingPosition) {
         EntityItemFrame entityitemframe = mapStack.getItemFrame();
         BlockPos blockpos = entityitemframe.getHangingPosition();
         MapFrame mapframe1 = this.frames.get(MapFrame.func_212766_a(blockpos));
         if (mapframe1 != null && entityitemframe.getEntityId() != mapframe1.getEntityId() && this.frames.containsKey(mapframe1.func_212767_e())) {
            this.mapDecorations.remove("frame-" + mapframe1.getEntityId());
         }

         MapFrame mapframe = new MapFrame(blockpos, entityitemframe.facingDirection.getHorizontalIndex() * 90, entityitemframe.getEntityId());
         this.updateDecorations(MapDecoration.Type.FRAME, player.world, "frame-" + entityitemframe.getEntityId(), (double)blockpos.getX(), (double)blockpos.getZ(), (double)(entityitemframe.facingDirection.getHorizontalIndex() * 90), (ITextComponent)null);
         this.frames.put(mapframe.func_212767_e(), mapframe);
      }

      NBTTagCompound nbttagcompound = mapStack.getTag();
      if (nbttagcompound != null && nbttagcompound.contains("Decorations", 9)) {
         NBTTagList nbttaglist = nbttagcompound.getList("Decorations", 10);

         for(int j = 0; j < nbttaglist.size(); ++j) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(j);
            if (!this.mapDecorations.containsKey(nbttagcompound1.getString("id"))) {
               this.updateDecorations(MapDecoration.Type.byIcon(nbttagcompound1.getByte("type")), player.world, nbttagcompound1.getString("id"), nbttagcompound1.getDouble("x"), nbttagcompound1.getDouble("z"), nbttagcompound1.getDouble("rot"), (ITextComponent)null);
            }
         }
      }

   }

   public static void addTargetDecoration(ItemStack map, BlockPos target, String decorationName, MapDecoration.Type type) {
      NBTTagList nbttaglist;
      if (map.hasTag() && map.getTag().contains("Decorations", 9)) {
         nbttaglist = map.getTag().getList("Decorations", 10);
      } else {
         nbttaglist = new NBTTagList();
         map.setTagInfo("Decorations", nbttaglist);
      }

      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.putByte("type", type.getIcon());
      nbttagcompound.putString("id", decorationName);
      nbttagcompound.putDouble("x", (double)target.getX());
      nbttagcompound.putDouble("z", (double)target.getZ());
      nbttagcompound.putDouble("rot", 180.0D);
      nbttaglist.add((INBTBase)nbttagcompound);
      if (type.hasMapColor()) {
         NBTTagCompound nbttagcompound1 = map.getOrCreateChildTag("display");
         nbttagcompound1.putInt("MapColor", type.getMapColor());
      }

   }

   private void updateDecorations(MapDecoration.Type type, @Nullable IWorld worldIn, String decorationName, double worldX, double worldZ, double rotationIn, @Nullable ITextComponent p_191095_10_) {
      int i = 1 << this.scale;
      float f = (float)(worldX - (double)this.xCenter) / (float)i;
      float f1 = (float)(worldZ - (double)this.zCenter) / (float)i;
      byte b0 = (byte)((int)((double)(f * 2.0F) + 0.5D));
      byte b1 = (byte)((int)((double)(f1 * 2.0F) + 0.5D));
      int j = 63;
      byte b2;
      if (f >= -63.0F && f1 >= -63.0F && f <= 63.0F && f1 <= 63.0F) {
         rotationIn = rotationIn + (rotationIn < 0.0D ? -8.0D : 8.0D);
         b2 = (byte)((int)(rotationIn * 16.0D / 360.0D));
         if (this.dimension == DimensionType.NETHER && worldIn != null) {
            int l = (int)(worldIn.getWorldInfo().getDayTime() / 10L);
            b2 = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
         }
      } else {
         if (type != MapDecoration.Type.PLAYER) {
            this.mapDecorations.remove(decorationName);
            return;
         }

         int k = 320;
         if (Math.abs(f) < 320.0F && Math.abs(f1) < 320.0F) {
            type = MapDecoration.Type.PLAYER_OFF_MAP;
         } else {
            if (!this.unlimitedTracking) {
               this.mapDecorations.remove(decorationName);
               return;
            }

            type = MapDecoration.Type.PLAYER_OFF_LIMITS;
         }

         b2 = 0;
         if (f <= -63.0F) {
            b0 = -128;
         }

         if (f1 <= -63.0F) {
            b1 = -128;
         }

         if (f >= 63.0F) {
            b0 = 127;
         }

         if (f1 >= 63.0F) {
            b1 = 127;
         }
      }

      this.mapDecorations.put(decorationName, new MapDecoration(type, b0, b1, b2, p_191095_10_));
   }

   @Nullable
   public Packet<?> getMapPacket(ItemStack mapStack, IBlockReader worldIn, EntityPlayer player) {
      MapData.MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
      return mapdata$mapinfo == null ? null : mapdata$mapinfo.getPacket(mapStack);
   }

   public void updateMapData(int x, int y) {
      this.markDirty();

      for(MapData.MapInfo mapdata$mapinfo : this.playersArrayList) {
         mapdata$mapinfo.update(x, y);
      }

   }

   public MapData.MapInfo getMapInfo(EntityPlayer player) {
      MapData.MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
      if (mapdata$mapinfo == null) {
         mapdata$mapinfo = new MapData.MapInfo(player);
         this.playersHashMap.put(player, mapdata$mapinfo);
         this.playersArrayList.add(mapdata$mapinfo);
      }

      return mapdata$mapinfo;
   }

   public void tryAddBanner(IWorld p_204269_1_, BlockPos p_204269_2_) {
      float f = (float)p_204269_2_.getX() + 0.5F;
      float f1 = (float)p_204269_2_.getZ() + 0.5F;
      int i = 1 << this.scale;
      float f2 = (f - (float)this.xCenter) / (float)i;
      float f3 = (f1 - (float)this.zCenter) / (float)i;
      int j = 63;
      boolean flag = false;
      if (f2 >= -63.0F && f3 >= -63.0F && f2 <= 63.0F && f3 <= 63.0F) {
         MapBanner mapbanner = MapBanner.fromWorld(p_204269_1_, p_204269_2_);
         if (mapbanner == null) {
            return;
         }

         boolean flag1 = true;
         if (this.banners.containsKey(mapbanner.getMapDecorationId()) && this.banners.get(mapbanner.getMapDecorationId()).equals(mapbanner)) {
            this.banners.remove(mapbanner.getMapDecorationId());
            this.mapDecorations.remove(mapbanner.getMapDecorationId());
            flag1 = false;
            flag = true;
         }

         if (flag1) {
            this.banners.put(mapbanner.getMapDecorationId(), mapbanner);
            this.updateDecorations(mapbanner.getDecorationType(), p_204269_1_, mapbanner.getMapDecorationId(), (double)f, (double)f1, 180.0D, mapbanner.getName());
            flag = true;
         }

         if (flag) {
            this.markDirty();
         }
      }

   }

   public void removeStaleBanners(IBlockReader p_204268_1_, int p_204268_2_, int p_204268_3_) {
      Iterator<MapBanner> iterator = this.banners.values().iterator();

      while(iterator.hasNext()) {
         MapBanner mapbanner = iterator.next();
         if (mapbanner.getPos().getX() == p_204268_2_ && mapbanner.getPos().getZ() == p_204268_3_) {
            MapBanner mapbanner1 = MapBanner.fromWorld(p_204268_1_, mapbanner.getPos());
            if (!mapbanner.equals(mapbanner1)) {
               iterator.remove();
               this.mapDecorations.remove(mapbanner.getMapDecorationId());
            }
         }
      }

   }

   public void removeItemFrame(BlockPos pos, int entityIdIn) {
      this.mapDecorations.remove("frame-" + entityIdIn);
      this.frames.remove(MapFrame.func_212766_a(pos));
   }

   public class MapInfo {
      public final EntityPlayer player;
      private boolean isDirty = true;
      private int minX;
      private int minY;
      private int maxX = 127;
      private int maxY = 127;
      private int tick;
      public int step;

      public MapInfo(EntityPlayer player) {
         this.player = player;
      }

      @Nullable
      public Packet<?> getPacket(ItemStack stack) {
         if (this.isDirty) {
            this.isDirty = false;
            return new SPacketMaps(ItemMap.getMapId(stack), MapData.this.scale, MapData.this.trackingPosition, MapData.this.mapDecorations.values(), MapData.this.colors, this.minX, this.minY, this.maxX + 1 - this.minX, this.maxY + 1 - this.minY);
         } else {
            return this.tick++ % 5 == 0 ? new SPacketMaps(ItemMap.getMapId(stack), MapData.this.scale, MapData.this.trackingPosition, MapData.this.mapDecorations.values(), MapData.this.colors, 0, 0, 0, 0) : null;
         }
      }

      public void update(int x, int y) {
         if (this.isDirty) {
            this.minX = Math.min(this.minX, x);
            this.minY = Math.min(this.minY, y);
            this.maxX = Math.max(this.maxX, x);
            this.maxY = Math.max(this.maxY, y);
         } else {
            this.isDirty = true;
            this.minX = x;
            this.minY = y;
            this.maxX = x;
            this.maxY = y;
         }

      }
   }
}
