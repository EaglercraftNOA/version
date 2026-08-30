package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Enchantment {
   private final EntityEquipmentSlot[] applicableEquipmentTypes;
   private final Enchantment.Rarity rarity;
   @Nullable
   public EnumEnchantmentType type;
   @Nullable
   protected String name;

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Enchantment getEnchantmentByID(int id) {
      return IRegistry.ENCHANTMENT.get(id);
   }

   protected Enchantment(Enchantment.Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
      this.rarity = rarityIn;
      this.type = typeIn;
      this.applicableEquipmentTypes = slots;
   }

   public List<ItemStack> getEntityEquipment(EntityLivingBase entityIn) {
      List<ItemStack> list = Lists.newArrayList();

      for(EntityEquipmentSlot entityequipmentslot : this.applicableEquipmentTypes) {
         ItemStack itemstack = entityIn.getItemStackFromSlot(entityequipmentslot);
         if (!itemstack.isEmpty()) {
            list.add(itemstack);
         }
      }

      return list;
   }

   public Enchantment.Rarity getRarity() {
      return this.rarity;
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return 1;
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + enchantmentLevel * 10;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 5;
   }

   public int calcModifierDamage(int level, DamageSource source) {
      return 0;
   }

   public float calcDamageByCreature(int level, CreatureAttribute creatureType) {
      return 0.0F;
   }

   public final boolean isCompatibleWith(Enchantment enchantmentIn) {
      return this.canApplyTogether(enchantmentIn) && enchantmentIn.canApplyTogether(this);
   }

   protected boolean canApplyTogether(Enchantment ench) {
      return this != ench;
   }

   protected String getDefaultTranslationKey() {
      if (this.name == null) {
         this.name = Util.makeTranslationKey("enchantment", IRegistry.ENCHANTMENT.getKey(this));
      }

      return this.name;
   }

   public String getName() {
      return this.getDefaultTranslationKey();
   }

   public ITextComponent getDisplayName(int level) {
      ITextComponent itextcomponent = new TextComponentTranslation(this.getName());
      if (this.isCurse()) {
         itextcomponent.applyTextStyle(TextFormatting.RED);
      } else {
         itextcomponent.applyTextStyle(TextFormatting.GRAY);
      }

      if (level != 1 || this.getMaxLevel() != 1) {
         itextcomponent.appendText(" ").appendSibling(new TextComponentTranslation("enchantment.level." + level));
      }

      return itextcomponent;
   }

   public boolean canApply(ItemStack stack) {
      return this.type.canEnchantItem(stack.getItem());
   }

   public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
   }

   public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {
   }

   public boolean isTreasureEnchantment() {
      return false;
   }

   public boolean isCurse() {
      return false;
   }

   public static void registerEnchantments() {
      EntityEquipmentSlot[] aentityequipmentslot = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
      register("protection", new EnchantmentProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.Type.ALL, aentityequipmentslot));
      register("fire_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.FIRE, aentityequipmentslot));
      register("feather_falling", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.FALL, aentityequipmentslot));
      register("blast_protection", new EnchantmentProtection(Enchantment.Rarity.RARE, EnchantmentProtection.Type.EXPLOSION, aentityequipmentslot));
      register("projectile_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.PROJECTILE, aentityequipmentslot));
      register("respiration", new EnchantmentOxygen(Enchantment.Rarity.RARE, aentityequipmentslot));
      register("aqua_affinity", new EnchantmentWaterWorker(Enchantment.Rarity.RARE, aentityequipmentslot));
      register("thorns", new EnchantmentThorns(Enchantment.Rarity.VERY_RARE, aentityequipmentslot));
      register("depth_strider", new EnchantmentWaterWalker(Enchantment.Rarity.RARE, aentityequipmentslot));
      register("frost_walker", new EnchantmentFrostWalker(Enchantment.Rarity.RARE, EntityEquipmentSlot.FEET));
      register("binding_curse", new EnchantmentBindingCurse(Enchantment.Rarity.VERY_RARE, aentityequipmentslot));
      register("sharpness", new EnchantmentDamage(Enchantment.Rarity.COMMON, 0, EntityEquipmentSlot.MAINHAND));
      register("smite", new EnchantmentDamage(Enchantment.Rarity.UNCOMMON, 1, EntityEquipmentSlot.MAINHAND));
      register("bane_of_arthropods", new EnchantmentDamage(Enchantment.Rarity.UNCOMMON, 2, EntityEquipmentSlot.MAINHAND));
      register("knockback", new EnchantmentKnockback(Enchantment.Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND));
      register("fire_aspect", new EnchantmentFireAspect(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("looting", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND));
      register("sweeping", new EnchantmentSweepingEdge(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("efficiency", new EnchantmentDigging(Enchantment.Rarity.COMMON, EntityEquipmentSlot.MAINHAND));
      register("silk_touch", new EnchantmentUntouching(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.MAINHAND));
      register("unbreaking", new EnchantmentDurability(Enchantment.Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND));
      register("fortune", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.DIGGER, EntityEquipmentSlot.MAINHAND));
      register("power", new EnchantmentArrowDamage(Enchantment.Rarity.COMMON, EntityEquipmentSlot.MAINHAND));
      register("punch", new EnchantmentArrowKnockback(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("flame", new EnchantmentArrowFire(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("infinity", new EnchantmentArrowInfinite(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.MAINHAND));
      register("luck_of_the_sea", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.FISHING_ROD, EntityEquipmentSlot.MAINHAND));
      register("lure", new EnchantmentFishingSpeed(Enchantment.Rarity.RARE, EnumEnchantmentType.FISHING_ROD, EntityEquipmentSlot.MAINHAND));
      register("loyalty", new EnchantmentLoyalty(Enchantment.Rarity.UNCOMMON, EntityEquipmentSlot.MAINHAND));
      register("impaling", new EnchantmentImpaling(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("riptide", new EnchantmentRiptide(Enchantment.Rarity.RARE, EntityEquipmentSlot.MAINHAND));
      register("channeling", new EnchantmentChanneling(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.MAINHAND));
      register("mending", new EnchantmentMending(Enchantment.Rarity.RARE, EntityEquipmentSlot.values()));
      register("vanishing_curse", new EnchantmentVanishingCurse(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.values()));
   }

   private static void register(String nameIn, Enchantment enchantmentIn) {
      IRegistry.ENCHANTMENT.put(new ResourceLocation(nameIn), enchantmentIn);
   }

   public static enum Rarity {
      COMMON(10),
      UNCOMMON(5),
      RARE(2),
      VERY_RARE(1);

      private final int weight;

      private Rarity(int rarityWeight) {
         this.weight = rarityWeight;
      }

      public int getWeight() {
         return this.weight;
      }
   }
}
