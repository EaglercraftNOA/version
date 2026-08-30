package net.minecraft.block;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockMelon extends Block {
	protected BlockMelon() {
		super(Material.GOURD, MapColor.LIME);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
		return Items.MELON;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(EaglercraftRandom random) {
		return 3 + random.nextInt(5);
	}

	/**
	 * Get the quantity dropped based on the given fortune level
	 */
	public int quantityDroppedWithBonus(int fortune, EaglercraftRandom random) {
		return Math.min(9, this.quantityDropped(random) + random.nextInt(1 + fortune));
	}
}
