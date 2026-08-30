package net.minecraft.src;

public class ItemPickaxe extends ItemTool {
	private static Block[] blocksEffectiveAgainst = new Block[]{Block.cobblestone, Block.stairDouble, Block.stairSingle, Block.stone, Block.cobblestoneMossy, Block.oreIron, Block.blockSteel, Block.oreCoal, Block.blockGold, Block.oreGold, Block.oreDiamond, Block.blockDiamond, Block.blockIce, Block.bloodStone};
	private int field_328_aY;

	public ItemPickaxe(int var1, int var2) {
		super(var1, 2, var2, blocksEffectiveAgainst);
		this.field_328_aY = var2;
	}

	public boolean canHarvestBlock(Block var1) {
		return var1 == Block.obsidian ? this.field_328_aY == 3 : (var1 != Block.blockDiamond && var1 != Block.oreDiamond ? (var1 != Block.blockGold && var1 != Block.oreGold ? (var1 != Block.blockSteel && var1 != Block.oreIron ? (var1 != Block.oreRedstone && var1 != Block.oreRedstoneGlowing ? (var1.blockMaterial == Material.rock ? true : var1.blockMaterial == Material.iron) : this.field_328_aY >= 2) : this.field_328_aY >= 1) : this.field_328_aY >= 2) : this.field_328_aY >= 2);
	}
}
