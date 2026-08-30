package net.minecraft.src;

public class CraftingInventoryPlayerCB extends CraftingInventoryCB {
	public InventoryCrafting craftMatrix;
	public IInventory craftResult;
	public boolean field_20124_c;

	public CraftingInventoryPlayerCB(InventoryPlayer var1) {
		this(var1, true);
	}

	public CraftingInventoryPlayerCB(InventoryPlayer var1, boolean var2) {
		this.craftMatrix = new InventoryCrafting(this, 2, 2);
		this.craftResult = new InventoryCraftResult();
		this.field_20124_c = false;
		this.field_20124_c = var2;
		this.func_20117_a(new SlotCrafting(this.craftMatrix, this.craftResult, 0, 144, 36));

		int var3;
		int var4;
		for(var3 = 0; var3 < 2; ++var3) {
			for(var4 = 0; var4 < 2; ++var4) {
				this.func_20117_a(new Slot(this.craftMatrix, var4 + var3 * 2, 88 + var4 * 18, 26 + var3 * 18));
			}
		}

		for(var3 = 0; var3 < 4; ++var3) {
			this.func_20117_a(new SlotArmor(this, var1, var1.getSizeInventory() - 1 - var3, 8, 8 + var3 * 18, var3));
		}

		for(var3 = 0; var3 < 3; ++var3) {
			for(var4 = 0; var4 < 9; ++var4) {
				this.func_20117_a(new Slot(var1, var4 + (var3 + 1) * 9, 8 + var4 * 18, 84 + var3 * 18));
			}
		}

		for(var3 = 0; var3 < 9; ++var3) {
			this.func_20117_a(new Slot(var1, var3, 8 + var3 * 18, 142));
		}

		this.onCraftMatrixChanged(this.craftMatrix);
	}

	public void onCraftMatrixChanged(IInventory var1) {
		int[] var2 = new int[9];

		for(int var3 = 0; var3 < 3; ++var3) {
			for(int var4 = 0; var4 < 3; ++var4) {
				int var5 = -1;
				if(var3 < 2 && var4 < 2) {
					ItemStack var6 = this.craftMatrix.getStackInSlot(var3 + var4 * 2);
					if(var6 != null) {
						var5 = var6.itemID;
					}
				}

				var2[var3 + var4 * 3] = var5;
			}
		}

		this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().craft(var2));
	}

	public void onCraftGuiClosed(EntityPlayer var1) {
		super.onCraftGuiClosed(var1);

		for(int var2 = 0; var2 < 4; ++var2) {
			ItemStack var3 = this.craftMatrix.getStackInSlot(var2);
			if(var3 != null) {
				var1.dropPlayerItem(var3);
				this.craftMatrix.setInventorySlotContents(var2, (ItemStack)null);
			}
		}

	}

	public boolean func_20120_b(EntityPlayer var1) {
		return true;
	}
}
