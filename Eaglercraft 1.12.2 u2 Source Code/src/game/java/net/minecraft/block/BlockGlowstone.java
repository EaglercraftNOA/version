package net.minecraft.block;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockGlowstone extends Block {
	public BlockGlowstone(Material materialIn) {
		super(materialIn);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	/**
	 * Get the quantity dropped based on the given fortune level
	 */
	public int quantityDroppedWithBonus(int fortune, EaglercraftRandom random) {
		return MathHelper.clamp(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 4);
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(EaglercraftRandom random) {
		return 2 + random.nextInt(3);
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
		return Items.GLOWSTONE_DUST;
	}

	/**
	 * Get the MapColor for this Block and the given BlockState
	 */
	public MapColor getMapColor(IBlockState state, IBlockAccess p_180659_2_, BlockPos p_180659_3_) {
		return MapColor.SAND;
	}
}
