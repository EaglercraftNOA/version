package net.minecraft.block;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockSnowBlock extends Block {
	protected BlockSnowBlock() {
		super(Material.CRAFTED_SNOW);
		this.setTickRandomly(true);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
		return Items.SNOWBALL;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(EaglercraftRandom random) {
		return 4;
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
		if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11) {
			this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
			worldIn.setBlockToAir(pos);
		}
	}
}
