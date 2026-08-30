package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTippedArrow extends ItemArrow {
   public ItemTippedArrow(Item.Properties builder) {
      super(builder);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getDefaultInstance() {
      return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), PotionTypes.POISON);
   }

   public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase p_200887_3_) {
      EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldIn, p_200887_3_);
      entitytippedarrow.setPotionEffect(stack);
      return entitytippedarrow;
   }

   public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
      if (this.isInGroup(group)) {
         for(PotionType potiontype : IRegistry.POTION) {
            if (!potiontype.getEffects().isEmpty()) {
               items.add(PotionUtils.addPotionToItemStack(new ItemStack(this), potiontype));
            }
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      PotionUtils.addPotionTooltip(stack, tooltip, 0.125F);
   }

   public String getTranslationKey(ItemStack stack) {
      return PotionUtils.getPotionFromItem(stack).getNamePrefixed(this.getTranslationKey() + ".effect.");
   }
}
