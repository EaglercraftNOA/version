package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractFish;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemBucketFish extends ItemBucket {
   private final EntityType<?> fishType;

   public ItemBucketFish(EntityType<?> fishTypeIn, Fluid p_i49022_2_, Item.Properties builder) {
      super(p_i49022_2_, builder);
      this.fishType = fishTypeIn;
   }

   public void onLiquidPlaced(World worldIn, ItemStack p_203792_2_, BlockPos pos) {
      if (!worldIn.isRemote) {
         this.placeFish(worldIn, p_203792_2_, pos);
      }

   }

   protected void playEmptySound(@Nullable EntityPlayer player, IWorld worldIn, BlockPos pos) {
      worldIn.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
   }

   private void placeFish(World worldIn, ItemStack p_205357_2_, BlockPos pos) {
      Entity entity = this.fishType.spawn(worldIn, p_205357_2_, (EntityPlayer)null, pos, true, false);
      if (entity != null) {
         ((AbstractFish)entity).setFromBucket(true);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      if (this.fishType == EntityType.TROPICAL_FISH) {
         NBTTagCompound nbttagcompound = stack.getTag();
         if (nbttagcompound != null && nbttagcompound.contains("BucketVariantTag", 3)) {
            int i = nbttagcompound.getInt("BucketVariantTag");
            TextFormatting[] atextformatting = new TextFormatting[]{TextFormatting.ITALIC, TextFormatting.GRAY};
            String s = "color.minecraft." + EntityTropicalFish.func_212326_d(i);
            String s1 = "color.minecraft." + EntityTropicalFish.func_212323_p(i);

            for(int j = 0; j < EntityTropicalFish.SPECIAL_VARIANTS.length; ++j) {
               if (i == EntityTropicalFish.SPECIAL_VARIANTS[j]) {
                  tooltip.add((new TextComponentTranslation(EntityTropicalFish.func_212324_b(j))).applyTextStyles(atextformatting));
                  return;
               }
            }

            tooltip.add((new TextComponentTranslation(EntityTropicalFish.func_212327_q(i))).applyTextStyles(atextformatting));
            ITextComponent itextcomponent = new TextComponentTranslation(s);
            if (!s.equals(s1)) {
               itextcomponent.appendText(", ").appendSibling(new TextComponentTranslation(s1));
            }

            itextcomponent.applyTextStyles(atextformatting);
            tooltip.add(itextcomponent);
         }
      }

   }
}
