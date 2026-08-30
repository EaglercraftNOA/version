package net.minecraft.block;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockBookshelf extends Block {
	public BlockBookshelf() {
		super(Material.WOOD);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(EaglercraftRandom random) {
		return 3;
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
		return Items.BOOK;
	}
}
