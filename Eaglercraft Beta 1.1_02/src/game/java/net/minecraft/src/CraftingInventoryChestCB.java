package net.minecraft.src;

public class CraftingInventoryChestCB extends CraftingInventoryCB {
	private IInventory field_20125_a;

	public CraftingInventoryChestCB(IInventory var1, IInventory var2) {
		this.field_20125_a = var2;
		int var3 = var2.getSizeInventory() / 9;
		int var4 = (var3 - 4) * 18;

		int var5;
		int var6;
		for(var5 = 0; var5 < var3; ++var5) {
			for(var6 = 0; var6 < 9; ++var6) {
				this.func_20117_a(new Slot(var2, var6 + var5 * 9, 8 + var6 * 18, 18 + var5 * 18));
			}
		}

		for(var5 = 0; var5 < 3; ++var5) {
			for(var6 = 0; var6 < 9; ++var6) {
				this.func_20117_a(new Slot(var1, var6 + var5 * 9 + 9, 8 + var6 * 18, 103 + var5 * 18 + var4));
			}
		}

		for(var5 = 0; var5 < 9; ++var5) {
			this.func_20117_a(new Slot(var1, var5, 8 + var5 * 18, 161 + var4));
		}

	}

	public boolean func_20120_b(EntityPlayer var1) {
		return this.field_20125_a.func_20070_a_(var1);
	}
}
