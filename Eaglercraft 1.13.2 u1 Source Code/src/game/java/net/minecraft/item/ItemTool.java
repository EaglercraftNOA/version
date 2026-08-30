package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTool extends ItemTiered {
   private final Set<Block> effectiveBlocks;
   protected float efficiency;
   protected float attackDamage;
   protected float attackSpeed;

   protected ItemTool(float attackDamageIn, float attackSpeedIn, IItemTier tier, Set<Block> effectiveBlocksIn, Item.Properties builder) {
      super(tier, builder);
      this.effectiveBlocks = effectiveBlocksIn;
      this.efficiency = tier.getEfficiency();
      this.attackDamage = attackDamageIn + tier.getAttackDamage();
      this.attackSpeed = attackSpeedIn;
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      return this.effectiveBlocks.contains(state.getBlock()) ? this.efficiency : 1.0F;
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      stack.damageItem(2, attacker);
      return true;
   }

   public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F) {
         stack.damageItem(1, entityLiving);
      }

      return true;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
      if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double)this.attackDamage, 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.attackSpeed, 0));
      }

      return multimap;
   }
}
