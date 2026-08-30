package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.EaglerUUIDHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ItemStack EMPTY = new ItemStack((Item)null);
   public static final DecimalFormat DECIMALFORMAT = createAttributeModifierDecimalFormat();
   private int count;
   private int animationsToGo;
   @Deprecated
   private final Item item;
   private NBTTagCompound tag;
   private boolean isEmpty;
   private EntityItemFrame itemFrame;
   private BlockWorldState canDestroyCacheBlock;
   private boolean canDestroyCacheResult;
   private BlockWorldState canPlaceOnCacheBlock;
   private boolean canPlaceOnCacheResult;

   private static DecimalFormat createAttributeModifierDecimalFormat() {
      DecimalFormat decimalformat = new DecimalFormat("#.##");
      decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
      return decimalformat;
   }

   public ItemStack(IItemProvider itemIn) {
      this(itemIn, 1);
   }

   public ItemStack(IItemProvider itemIn, int count) {
      this.item = itemIn == null ? null : itemIn.asItem();
      this.count = count;
      this.updateEmptyState();
   }

   private void updateEmptyState() {
      this.isEmpty = false;
      this.isEmpty = this.isEmpty();
   }

   private ItemStack(NBTTagCompound compound) {
      Item item = IRegistry.ITEM.get(new ResourceLocation(compound.getString("id")));
      this.item = item == null ? Items.AIR : item;
      this.count = compound.getByte("Count");
      if (compound.contains("tag", 10)) {
         this.tag = compound.getCompound("tag");
         this.getItem().updateItemStackNBT(compound);
      }

      if (this.getItem().isDamageable()) {
         this.setDamage(this.getDamage());
      }

      this.updateEmptyState();
   }

   public static ItemStack read(NBTTagCompound compound) {
      try {
         return new ItemStack(compound);
      } catch (RuntimeException runtimeexception) {
         LOGGER.debug("Tried to load invalid item: {}", compound, runtimeexception);
         return EMPTY;
      }
   }

   public boolean isEmpty() {
      if (this == EMPTY) {
         return true;
      } else if (this.getItem() != null && this.getItem() != Items.AIR) {
         return this.count <= 0;
      } else {
         return true;
      }
   }

   public ItemStack split(int amount) {
      int i = Math.min(amount, this.count);
      ItemStack itemstack = this.copy();
      itemstack.setCount(i);
      this.shrink(i);
      return itemstack;
   }

   public Item getItem() {
      return this.isEmpty ? Items.AIR : this.item;
   }

   public EnumActionResult onItemUse(ItemUseContext context) {
      EntityPlayer entityplayer = context.getPlayer();
      BlockPos blockpos = context.getPos();
      BlockWorldState blockworldstate = new BlockWorldState(context.getWorld(), blockpos, false);
      if (entityplayer != null && !entityplayer.abilities.allowEdit && !this.canPlaceOn(context.getWorld().getTags(), blockworldstate)) {
         return EnumActionResult.PASS;
      } else {
         Item item = this.getItem();
         EnumActionResult enumactionresult = item.onItemUse(context);
         if (entityplayer != null && enumactionresult == EnumActionResult.SUCCESS) {
            entityplayer.addStat(StatList.ITEM_USED.get(item));
         }

         return enumactionresult;
      }
   }

   public float getDestroySpeed(IBlockState blockIn) {
      return this.getItem().getDestroySpeed(this, blockIn);
   }

   public ActionResult<ItemStack> useItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
      return this.getItem().onItemRightClick(worldIn, playerIn, hand);
   }

   public ItemStack onItemUseFinish(World worldIn, EntityLivingBase entityLiving) {
      return this.getItem().onItemUseFinish(this, worldIn, entityLiving);
   }

   public NBTTagCompound write(NBTTagCompound nbt) {
      ResourceLocation resourcelocation = IRegistry.ITEM.getKey(this.getItem());
      nbt.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
      nbt.putByte("Count", (byte)this.count);
      if (this.tag != null) {
         nbt.put("tag", this.tag);
      }

      return nbt;
   }

   public int getMaxStackSize() {
      return this.getItem().getMaxStackSize();
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isDamageable() || !this.isDamaged());
   }

   public boolean isDamageable() {
      if (!this.isEmpty && this.getItem().getMaxDamage() > 0) {
         NBTTagCompound nbttagcompound = this.getTag();
         return nbttagcompound == null || !nbttagcompound.getBoolean("Unbreakable");
      } else {
         return false;
      }
   }

   public boolean isDamaged() {
      return this.isDamageable() && this.getDamage() > 0;
   }

   public int getDamage() {
      return this.tag == null ? 0 : this.tag.getInt("Damage");
   }

   public void setDamage(int damage) {
      this.getOrCreateTag().putInt("Damage", Math.max(0, damage));
   }

   public int getMaxDamage() {
      return this.getItem().getMaxDamage();
   }

   public boolean attemptDamageItem(int amount, Random rand, @Nullable EntityPlayerMP damager) {
      if (!this.isDamageable()) {
         return false;
      } else {
         if (amount > 0) {
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, this);
            int j = 0;

            for(int k = 0; i > 0 && k < amount; ++k) {
               if (EnchantmentDurability.negateDamage(this, i, rand)) {
                  ++j;
               }
            }

            amount -= j;
            if (amount <= 0) {
               return false;
            }
         }

         if (damager != null && amount != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(damager, this, this.getDamage() + amount);
         }

         int l = this.getDamage() + amount;
         this.setDamage(l);
         return l >= this.getMaxDamage();
      }
   }

   public void damageItem(int amount, EntityLivingBase entityIn) {
      if (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).abilities.isCreativeMode) {
         if (this.isDamageable()) {
            if (this.attemptDamageItem(amount, entityIn.getRNG(), entityIn instanceof EntityPlayerMP ? (EntityPlayerMP)entityIn : null)) {
               entityIn.renderBrokenItemStack(this);
               Item item = this.getItem();
               this.shrink(1);
               if (entityIn instanceof EntityPlayer) {
                  ((EntityPlayer)entityIn).addStat(StatList.ITEM_BROKEN.get(item));
               }

               this.setDamage(0);
            }

         }
      }
   }

   public void hitEntity(EntityLivingBase entityIn, EntityPlayer playerIn) {
      Item item = this.getItem();
      if (item.hitEntity(this, entityIn, playerIn)) {
         playerIn.addStat(StatList.ITEM_USED.get(item));
      }

   }

   public void onBlockDestroyed(World worldIn, IBlockState blockIn, BlockPos pos, EntityPlayer playerIn) {
      Item item = this.getItem();
      if (item.onBlockDestroyed(this, worldIn, blockIn, pos, playerIn)) {
         playerIn.addStat(StatList.ITEM_USED.get(item));
      }

   }

   public boolean canHarvestBlock(IBlockState blockIn) {
      return this.getItem().canHarvestBlock(blockIn);
   }

   public boolean interactWithEntity(EntityPlayer playerIn, EntityLivingBase entityIn, EnumHand hand) {
      return this.getItem().itemInteractionForEntity(this, playerIn, entityIn, hand);
   }

   public ItemStack copy() {
      ItemStack itemstack = new ItemStack(this.getItem(), this.count);
      itemstack.setAnimationsToGo(this.getAnimationsToGo());
      if (this.tag != null) {
         itemstack.tag = this.tag.copy();
      }

      return itemstack;
   }

   public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
      if (stackA.isEmpty() && stackB.isEmpty()) {
         return true;
      } else if (!stackA.isEmpty() && !stackB.isEmpty()) {
         if (stackA.tag == null && stackB.tag != null) {
            return false;
         } else {
            return stackA.tag == null || stackA.tag.equals(stackB.tag);
         }
      } else {
         return false;
      }
   }

   public static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {
      if (stackA.isEmpty() && stackB.isEmpty()) {
         return true;
      } else {
         return !stackA.isEmpty() && !stackB.isEmpty() ? stackA.isItemStackEqual(stackB) : false;
      }
   }

   private boolean isItemStackEqual(ItemStack other) {
      if (this.count != other.count) {
         return false;
      } else if (this.getItem() != other.getItem()) {
         return false;
      } else if (this.tag == null && other.tag != null) {
         return false;
      } else {
         return this.tag == null || this.tag.equals(other.tag);
      }
   }

   public static boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
      if (stackA == stackB) {
         return true;
      } else {
         return !stackA.isEmpty() && !stackB.isEmpty() ? stackA.isItemEqual(stackB) : false;
      }
   }

   public static boolean areItemsEqualIgnoreDurability(ItemStack stackA, ItemStack stackB) {
      if (stackA == stackB) {
         return true;
      } else {
         return !stackA.isEmpty() && !stackB.isEmpty() ? stackA.isItemEqualIgnoreDurability(stackB) : false;
      }
   }

   public boolean isItemEqual(ItemStack other) {
      return !other.isEmpty() && this.getItem() == other.getItem();
   }

   public boolean isItemEqualIgnoreDurability(ItemStack stack) {
      if (!this.isDamageable()) {
         return this.isItemEqual(stack);
      } else {
         return !stack.isEmpty() && this.getItem() == stack.getItem();
      }
   }

   public String getTranslationKey() {
      return this.getItem().getTranslationKey(this);
   }

   public String toString() {
      return this.count + "x" + this.getItem().getTranslationKey();
   }

   public void inventoryTick(World worldIn, Entity entityIn, int inventorySlot, boolean isCurrentItem) {
      if (this.animationsToGo > 0) {
         --this.animationsToGo;
      }

      if (this.getItem() != null) {
         this.getItem().inventoryTick(this, worldIn, entityIn, inventorySlot, isCurrentItem);
      }

   }

   public void onCrafting(World worldIn, EntityPlayer playerIn, int amount) {
      playerIn.addStat(StatList.ITEM_CRAFTED.get(this.getItem()), amount);
      this.getItem().onCreated(this, worldIn, playerIn);
   }

   public int getUseDuration() {
      return this.getItem().getUseDuration(this);
   }

   public EnumAction getUseAction() {
      return this.getItem().getUseAction(this);
   }

   public void onPlayerStoppedUsing(World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      this.getItem().onPlayerStoppedUsing(this, worldIn, entityLiving, timeLeft);
   }

   public boolean hasTag() {
      return !this.isEmpty && this.tag != null && !this.tag.isEmpty();
   }

   @Nullable
   public NBTTagCompound getTag() {
      return this.tag;
   }

   public NBTTagCompound getOrCreateTag() {
      if (this.tag == null) {
         this.setTag(new NBTTagCompound());
      }

      return this.tag;
   }

   public NBTTagCompound getOrCreateChildTag(String key) {
      if (this.tag != null && this.tag.contains(key, 10)) {
         return this.tag.getCompound(key);
      } else {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         this.setTagInfo(key, nbttagcompound);
         return nbttagcompound;
      }
   }

   @Nullable
   public NBTTagCompound getChildTag(String key) {
      return this.tag != null && this.tag.contains(key, 10) ? this.tag.getCompound(key) : null;
   }

   public void removeChildTag(String p_196083_1_) {
      if (this.tag != null && this.tag.contains(p_196083_1_)) {
         this.tag.remove(p_196083_1_);
         if (this.tag.isEmpty()) {
            this.tag = null;
         }
      }

   }

   public NBTTagList getEnchantmentTagList() {
      return this.tag != null ? this.tag.getList("Enchantments", 10) : new NBTTagList();
   }

   public void setTag(@Nullable NBTTagCompound nbt) {
      this.tag = nbt;
   }

   public ITextComponent getDisplayName() {
      NBTTagCompound nbttagcompound = this.getChildTag("display");
      if (nbttagcompound != null && nbttagcompound.contains("Name", 8)) {
         try {
            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(nbttagcompound.getString("Name"));
            if (itextcomponent != null) {
               return itextcomponent;
            }

            nbttagcompound.remove("Name");
         } catch (JsonParseException var3) {
            nbttagcompound.remove("Name");
         }
      }

      return this.getItem().getDisplayName(this);
   }

   public ItemStack setDisplayName(@Nullable ITextComponent name) {
      NBTTagCompound nbttagcompound = this.getOrCreateChildTag("display");
      if (name != null) {
         nbttagcompound.putString("Name", ITextComponent.Serializer.toJson(name));
      } else {
         nbttagcompound.remove("Name");
      }

      return this;
   }

   public void clearCustomName() {
      NBTTagCompound nbttagcompound = this.getChildTag("display");
      if (nbttagcompound != null) {
         nbttagcompound.remove("Name");
         if (nbttagcompound.isEmpty()) {
            this.removeChildTag("display");
         }
      }

      if (this.tag != null && this.tag.isEmpty()) {
         this.tag = null;
      }

   }

   public boolean hasDisplayName() {
      NBTTagCompound nbttagcompound = this.getChildTag("display");
      return nbttagcompound != null && nbttagcompound.contains("Name", 8);
   }

   @OnlyIn(Dist.CLIENT)
   public List<ITextComponent> getTooltip(@Nullable EntityPlayer playerIn, ITooltipFlag advanced) {
      List<ITextComponent> list = Lists.newArrayList();
      ITextComponent itextcomponent = (new TextComponentString("")).appendSibling(this.getDisplayName()).applyTextStyle(this.getRarity().color);
      if (this.hasDisplayName()) {
         itextcomponent.applyTextStyle(TextFormatting.ITALIC);
      }

      list.add(itextcomponent);
      if (!advanced.isAdvanced() && !this.hasDisplayName() && this.getItem() == Items.FILLED_MAP) {
         list.add((new TextComponentString("#" + ItemMap.getMapId(this))).applyTextStyle(TextFormatting.GRAY));
      }

      int i = 0;
      if (this.hasTag() && this.tag.contains("HideFlags", 99)) {
         i = this.tag.getInt("HideFlags");
      }

      if ((i & 32) == 0) {
         this.getItem().addInformation(this, playerIn == null ? null : playerIn.world, list, advanced);
      }

      if (this.hasTag()) {
         if ((i & 1) == 0) {
            NBTTagList nbttaglist = this.getEnchantmentTagList();

            for(int j = 0; j < nbttaglist.size(); ++j) {
               NBTTagCompound nbttagcompound = nbttaglist.getCompound(j);
               Enchantment enchantment = IRegistry.ENCHANTMENT.get(ResourceLocation.tryCreate(nbttagcompound.getString("id")));
               if (enchantment != null) {
                  list.add(enchantment.getDisplayName(nbttagcompound.getInt("lvl")));
               }
            }
         }

         if (this.tag.contains("display", 10)) {
            NBTTagCompound nbttagcompound1 = this.tag.getCompound("display");
            if (nbttagcompound1.contains("color", 3)) {
               if (advanced.isAdvanced()) {
                  list.add((new TextComponentTranslation("item.color", String.format("#%06X", nbttagcompound1.getInt("color")))).applyTextStyle(TextFormatting.GRAY));
               } else {
                  list.add((new TextComponentTranslation("item.dyed")).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
               }
            }

            if (nbttagcompound1.getTagId("Lore") == 9) {
               NBTTagList nbttaglist3 = nbttagcompound1.getList("Lore", 8);

               for(int i1 = 0; i1 < nbttaglist3.size(); ++i1) {
                  list.add((new TextComponentString(nbttaglist3.getString(i1))).applyTextStyles(new TextFormatting[]{TextFormatting.DARK_PURPLE, TextFormatting.ITALIC}));
               }
            }
         }
      }

      for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
         Multimap<String, AttributeModifier> multimap = this.getAttributeModifiers(entityequipmentslot);
         if (!multimap.isEmpty() && (i & 2) == 0) {
            list.add(new TextComponentString(""));
            list.add((new TextComponentTranslation("item.modifiers." + entityequipmentslot.getName())).applyTextStyle(TextFormatting.GRAY));

            for(Entry<String, AttributeModifier> entry : multimap.entries()) {
               AttributeModifier attributemodifier = entry.getValue();
               double d0 = attributemodifier.getAmount();
               boolean flag = false;
               if (playerIn != null) {
                  if (attributemodifier.getID() == Item.ATTACK_DAMAGE_MODIFIER) {
                     d0 = d0 + playerIn.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                     d0 = d0 + (double)EnchantmentHelper.getModifierForCreature(this, CreatureAttribute.UNDEFINED);
                     flag = true;
                  } else if (attributemodifier.getID() == Item.ATTACK_SPEED_MODIFIER) {
                     d0 += playerIn.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
                     flag = true;
                  }
               }

               double d1;
               if (attributemodifier.getOperation() != 1 && attributemodifier.getOperation() != 2) {
                  d1 = d0;
               } else {
                  d1 = d0 * 100.0D;
               }

               if (flag) {
                  list.add((new TextComponentString(" ")).appendSibling(new TextComponentTranslation("attribute.modifier.equals." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1), new TextComponentTranslation("attribute.name." + (String)entry.getKey()))).applyTextStyle(TextFormatting.DARK_GREEN));
               } else if (d0 > 0.0D) {
                  list.add((new TextComponentTranslation("attribute.modifier.plus." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1), new TextComponentTranslation("attribute.name." + (String)entry.getKey()))).applyTextStyle(TextFormatting.BLUE));
               } else if (d0 < 0.0D) {
                  d1 = d1 * -1.0D;
                  list.add((new TextComponentTranslation("attribute.modifier.take." + attributemodifier.getOperation(), DECIMALFORMAT.format(d1), new TextComponentTranslation("attribute.name." + (String)entry.getKey()))).applyTextStyle(TextFormatting.RED));
               }
            }
         }
      }

      if (this.hasTag() && this.getTag().getBoolean("Unbreakable") && (i & 4) == 0) {
         list.add((new TextComponentTranslation("item.unbreakable")).applyTextStyle(TextFormatting.BLUE));
      }

      if (this.hasTag() && this.tag.contains("CanDestroy", 9) && (i & 8) == 0) {
         NBTTagList nbttaglist1 = this.tag.getList("CanDestroy", 8);
         if (!nbttaglist1.isEmpty()) {
            list.add(new TextComponentString(""));
            list.add((new TextComponentTranslation("item.canBreak")).applyTextStyle(TextFormatting.GRAY));

            for(int k = 0; k < nbttaglist1.size(); ++k) {
               list.addAll(getPlacementTooltip(nbttaglist1.getString(k)));
            }
         }
      }

      if (this.hasTag() && this.tag.contains("CanPlaceOn", 9) && (i & 16) == 0) {
         NBTTagList nbttaglist2 = this.tag.getList("CanPlaceOn", 8);
         if (!nbttaglist2.isEmpty()) {
            list.add(new TextComponentString(""));
            list.add((new TextComponentTranslation("item.canPlace")).applyTextStyle(TextFormatting.GRAY));

            for(int l = 0; l < nbttaglist2.size(); ++l) {
               list.addAll(getPlacementTooltip(nbttaglist2.getString(l)));
            }
         }
      }

      if (advanced.isAdvanced()) {
         if (this.isDamaged()) {
            list.add(new TextComponentTranslation("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
         }

         list.add((new TextComponentString(IRegistry.ITEM.getKey(this.getItem()).toString())).applyTextStyle(TextFormatting.DARK_GRAY));
         if (this.hasTag()) {
            list.add((new TextComponentTranslation("item.nbt_tags", this.getTag().keySet().size())).applyTextStyle(TextFormatting.DARK_GRAY));
         }
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   private static Collection<ITextComponent> getPlacementTooltip(String stateString) {
      try {
         BlockStateParser blockstateparser = (new BlockStateParser(new StringReader(stateString), true)).parse(true);
         IBlockState iblockstate = blockstateparser.getState();
         ResourceLocation resourcelocation = blockstateparser.getTag();
         boolean flag = iblockstate != null;
         boolean flag1 = resourcelocation != null;
         if (flag || flag1) {
            if (flag) {
               return Lists.newArrayList(iblockstate.getBlock().getNameTextComponent().applyTextStyle(TextFormatting.DARK_GRAY));
            }

            Tag<Block> tag = BlockTags.getCollection().get(resourcelocation);
            if (tag != null) {
               Collection<Block> collection = tag.getAllElements();
               if (!collection.isEmpty()) {
                  return collection.stream().map(Block::getNameTextComponent).map((p_211702_0_) -> {
                     return p_211702_0_.applyTextStyle(TextFormatting.DARK_GRAY);
                  }).collect(Collectors.toList());
               }
            }
         }
      } catch (CommandSyntaxException var8) {
         ;
      }

      return Lists.newArrayList((new TextComponentString("missingno")).applyTextStyle(TextFormatting.DARK_GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect() {
      return this.getItem().hasEffect(this);
   }

   public EnumRarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isEnchantable() {
      if (!this.getItem().isEnchantable(this)) {
         return false;
      } else {
         return !this.isEnchanted();
      }
   }

   public void addEnchantment(Enchantment ench, int level) {
      this.getOrCreateTag();
      if (!this.tag.contains("Enchantments", 9)) {
         this.tag.put("Enchantments", new NBTTagList());
      }

      NBTTagList nbttaglist = this.tag.getList("Enchantments", 10);
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.putString("id", String.valueOf((Object)IRegistry.ENCHANTMENT.getKey(ench)));
      nbttagcompound.putShort("lvl", (short)((byte)level));
      nbttaglist.add((INBTBase)nbttagcompound);
   }

   public boolean isEnchanted() {
      if (this.tag != null && this.tag.contains("Enchantments", 9)) {
         return !this.tag.getList("Enchantments", 10).isEmpty();
      } else {
         return false;
      }
   }

   public void setTagInfo(String key, INBTBase value) {
      this.getOrCreateTag().put(key, value);
   }

   public boolean isOnItemFrame() {
      return this.itemFrame != null;
   }

   public void setItemFrame(@Nullable EntityItemFrame frame) {
      this.itemFrame = frame;
   }

   @Nullable
   public EntityItemFrame getItemFrame() {
      return this.isEmpty ? null : this.itemFrame;
   }

   public int getRepairCost() {
      return this.hasTag() && this.tag.contains("RepairCost", 3) ? this.tag.getInt("RepairCost") : 0;
   }

   public void setRepairCost(int cost) {
      this.getOrCreateTag().putInt("RepairCost", cost);
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap;
      if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
         multimap = HashMultimap.create();
         NBTTagList nbttaglist = this.tag.getList("AttributeModifiers", 10);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            AttributeModifier attributemodifier = SharedMonsterAttributes.readAttributeModifier(nbttagcompound);
            if (attributemodifier != null && (!nbttagcompound.contains("Slot", 8) || nbttagcompound.getString("Slot").equals(equipmentSlot.getName())) && EaglerUUIDHelper.getLeastSignificantBits(attributemodifier.getID()) != 0L && EaglerUUIDHelper.getMostSignificantBits(attributemodifier.getID()) != 0L) {
               multimap.put(nbttagcompound.getString("AttributeName"), attributemodifier);
            }
         }
      } else {
         multimap = this.getItem().getAttributeModifiers(equipmentSlot);
      }

      return multimap;
   }

   public void addAttributeModifier(String attributeName, AttributeModifier modifier, @Nullable EntityEquipmentSlot equipmentSlot) {
      this.getOrCreateTag();
      if (!this.tag.contains("AttributeModifiers", 9)) {
         this.tag.put("AttributeModifiers", new NBTTagList());
      }

      NBTTagList nbttaglist = this.tag.getList("AttributeModifiers", 10);
      NBTTagCompound nbttagcompound = SharedMonsterAttributes.writeAttributeModifier(modifier);
      nbttagcompound.putString("AttributeName", attributeName);
      if (equipmentSlot != null) {
         nbttagcompound.putString("Slot", equipmentSlot.getName());
      }

      nbttaglist.add((INBTBase)nbttagcompound);
   }

   public ITextComponent getTextComponent() {
      ITextComponent itextcomponent = (new TextComponentString("")).appendSibling(this.getDisplayName());
      if (this.hasDisplayName()) {
         itextcomponent.applyTextStyle(TextFormatting.ITALIC);
      }

      ITextComponent itextcomponent1 = TextComponentUtils.wrapInSquareBrackets(itextcomponent);
      if (!this.isEmpty) {
         NBTTagCompound nbttagcompound = this.write(new NBTTagCompound());
         itextcomponent1.applyTextStyle(this.getRarity().color).applyTextStyle((p_211700_1_) -> {
            p_211700_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponentString(nbttagcompound.toString())));
         });
      }

      return itextcomponent1;
   }

   private static boolean isStateAndTileEntityEqual(BlockWorldState p_206846_0_, @Nullable BlockWorldState p_206846_1_) {
      if (p_206846_1_ != null && p_206846_0_.getBlockState() == p_206846_1_.getBlockState()) {
         if (p_206846_0_.getTileEntity() == null && p_206846_1_.getTileEntity() == null) {
            return true;
         } else {
            return p_206846_0_.getTileEntity() != null && p_206846_1_.getTileEntity() != null ? Objects.equals(p_206846_0_.getTileEntity().write(new NBTTagCompound()), p_206846_1_.getTileEntity().write(new NBTTagCompound())) : false;
         }
      } else {
         return false;
      }
   }

   public boolean canDestroy(NetworkTagManager p_206848_1_, BlockWorldState p_206848_2_) {
      if (isStateAndTileEntityEqual(p_206848_2_, this.canDestroyCacheBlock)) {
         return this.canDestroyCacheResult;
      } else {
         this.canDestroyCacheBlock = p_206848_2_;
         if (this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            NBTTagList nbttaglist = this.tag.getList("CanDestroy", 8);

            for(int i = 0; i < nbttaglist.size(); ++i) {
               String s = nbttaglist.getString(i);

               try {
                  Predicate<BlockWorldState> predicate = BlockPredicateArgument.blockPredicate().parse(new StringReader(s)).create(p_206848_1_);
                  if (predicate.test(p_206848_2_)) {
                     this.canDestroyCacheResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
                  ;
               }
            }
         }

         this.canDestroyCacheResult = false;
         return false;
      }
   }

   public boolean canPlaceOn(NetworkTagManager p_206847_1_, BlockWorldState p_206847_2_) {
      if (isStateAndTileEntityEqual(p_206847_2_, this.canPlaceOnCacheBlock)) {
         return this.canPlaceOnCacheResult;
      } else {
         this.canPlaceOnCacheBlock = p_206847_2_;
         if (this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            NBTTagList nbttaglist = this.tag.getList("CanPlaceOn", 8);

            for(int i = 0; i < nbttaglist.size(); ++i) {
               String s = nbttaglist.getString(i);

               try {
                  Predicate<BlockWorldState> predicate = BlockPredicateArgument.blockPredicate().parse(new StringReader(s)).create(p_206847_1_);
                  if (predicate.test(p_206847_2_)) {
                     this.canPlaceOnCacheResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
                  ;
               }
            }
         }

         this.canPlaceOnCacheResult = false;
         return false;
      }
   }

   public int getAnimationsToGo() {
      return this.animationsToGo;
   }

   public void setAnimationsToGo(int animations) {
      this.animationsToGo = animations;
   }

   public int getCount() {
      return this.isEmpty ? 0 : this.count;
   }

   public void setCount(int count) {
      this.count = count;
      this.updateEmptyState();
   }

   public void grow(int count) {
      this.setCount(this.count + count);
   }

   public void shrink(int count) {
      this.grow(-count);
   }
}
