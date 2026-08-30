package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemShield extends Item {
   public ItemShield(Item.Properties builder) {
      super(builder);
      this.addPropertyOverride(new ResourceLocation("blocking"), (p_210314_0_, p_210314_1_, p_210314_2_) -> {
         return p_210314_2_ != null && p_210314_2_.isHandActive() && p_210314_2_.getActiveItemStack() == p_210314_0_ ? 1.0F : 0.0F;
      });
      BlockDispenser.registerDispenseBehavior(this, ItemArmor.DISPENSER_BEHAVIOR);
   }

   public String getTranslationKey(ItemStack stack) {
      return stack.getChildTag("BlockEntityTag") != null ? this.getTranslationKey() + '.' + getColor(stack).getTranslationKey() : super.getTranslationKey(stack);
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      ItemBanner.appendHoverTextFromTileEntityTag(stack, tooltip);
   }

   public EnumAction getUseAction(ItemStack stack) {
      return EnumAction.BLOCK;
   }

   public int getUseDuration(ItemStack stack) {
      return 72000;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      playerIn.setActiveHand(handIn);
      return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return ItemTags.PLANKS.contains(repair.getItem()) || super.getIsRepairable(toRepair, repair);
   }

   public static EnumDyeColor getColor(ItemStack stack) {
      return EnumDyeColor.byId(stack.getOrCreateChildTag("BlockEntityTag").getInt("Base"));
   }
}
