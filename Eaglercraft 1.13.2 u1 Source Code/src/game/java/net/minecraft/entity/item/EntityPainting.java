package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityPainting extends EntityHanging {
   public PaintingType art;

   public EntityPainting(World worldIn) {
      super(EntityType.PAINTING, worldIn);
   }

   public EntityPainting(World worldIn, BlockPos pos, EnumFacing facing) {
      super(EntityType.PAINTING, worldIn, pos);
      List<PaintingType> list = Lists.newArrayList();
      int i = 0;

      for(PaintingType paintingtype : IRegistry.MOTIVE) {
         this.art = paintingtype;
         this.updateFacingWithBoundingBox(facing);
         if (this.onValidSurface()) {
            list.add(paintingtype);
            int j = paintingtype.getWidth() * paintingtype.getHeight();
            if (j > i) {
               i = j;
            }
         }
      }

      if (!list.isEmpty()) {
         Iterator<PaintingType> iterator = list.iterator();

         while(iterator.hasNext()) {
            PaintingType paintingtype1 = iterator.next();
            if (paintingtype1.getWidth() * paintingtype1.getHeight() < i) {
               iterator.remove();
            }
         }

         this.art = list.get(this.rand.nextInt(list.size()));
      }

      this.updateFacingWithBoundingBox(facing);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityPainting(World p_i48559_1_, BlockPos p_i48559_2_, EnumFacing p_i48559_3_, PaintingType p_i48559_4_) {
      this(p_i48559_1_, p_i48559_2_, p_i48559_3_);
      this.art = p_i48559_4_;
      this.updateFacingWithBoundingBox(p_i48559_3_);
   }

   public void writeAdditional(NBTTagCompound compound) {
      compound.putString("Motive", IRegistry.MOTIVE.getKey(this.art).toString());
      super.writeAdditional(compound);
   }

   public void readAdditional(NBTTagCompound compound) {
      this.art = IRegistry.MOTIVE.getOrDefault(ResourceLocation.tryCreate(compound.getString("Motive")));
      super.readAdditional(compound);
   }

   public int getWidthPixels() {
      return this.art.getWidth();
   }

   public int getHeightPixels() {
      return this.art.getHeight();
   }

   public void onBroken(@Nullable Entity brokenEntity) {
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
         if (brokenEntity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)brokenEntity;
            if (entityplayer.abilities.isCreativeMode) {
               return;
            }
         }

         this.entityDropItem(Items.PAINTING);
      }
   }

   public void playPlaceSound() {
      this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
   }

   public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.setPosition(x, y, z);
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      BlockPos blockpos = this.hangingPosition.add(x - this.posX, y - this.posY, z - this.posZ);
      this.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
   }
}
