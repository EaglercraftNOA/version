package net.minecraft.src;

public class CraftingInventoryWorkbenchCB extends CraftingInventoryCB {
	public InventoryCrafting a = new InventoryCrafting(this, 3, 3);
	public IInventory b = new InventoryCraftResult();
	private World field_20133_c;
	private int field_20132_h;
	private int field_20131_i;
	private int field_20130_j;

	public CraftingInventoryWorkbenchCB(InventoryPlayer var1, World var2, int var3, int var4, int var5) {
		this.field_20133_c = var2;
		this.field_20132_h = var3;
		this.field_20131_i = var4;
		this.field_20130_j = var5;
		this.func_20117_a(new SlotCrafting(this.a, this.b, 0, 124, 35));

		int var6;
		int var7;
		for(var6 = 0; var6 < 3; ++var6) {
			for(var7 = 0; var7 < 3; ++var7) {
				this.func_20117_a(new Slot(this.a, var7 + var6 * 3, 30 + var7 * 18, 17 + var6 * 18));
			}
		}

		for(var6 = 0; var6 < 3; ++var6) {
			for(var7 = 0; var7 < 9; ++var7) {
				this.func_20117_a(new Slot(var1, var7 + var6 * 9 + 9, 8 + var7 * 18, 84 + var6 * 18));
			}
		}

		for(var6 = 0; var6 < 9; ++var6) {
			this.func_20117_a(new Slot(var1, var6, 8 + var6 * 18, 142));
		}

		this.onCraftMatrixChanged(this.a);
	}

	public void onCraftMatrixChanged(IInventory var1) {
		int[] var2 = new int[9];

		for(int var3 = 0; var3 < 3; ++var3) {
			for(int var4 = 0; var4 < 3; ++var4) {
				int var5 = var3 + var4 * 3;
				ItemStack var6 = this.a.getStackInSlot(var5);
				if(var6 == null) {
					var2[var5] = -1;
				} else {
					var2[var5] = var6.itemID;
				}
			}
		}

		this.b.setInventorySlotContents(0, CraftingManager.getInstance().craft(var2));
	}

	public void onCraftGuiClosed(EntityPlayer var1) {
		super.onCraftGuiClosed(var1);

		for(int var2 = 0; var2 < 9; ++var2) {
			ItemStack var3 = this.a.getStackInSlot(var2);
			if(var3 != null) {
				var1.dropPlayerItem(var3);
			}
		}

	}

	public boolean func_20120_b(EntityPlayer var1) {
		return this.field_20133_c.getBlockId(this.field_20132_h, this.field_20131_i, this.field_20130_j) != Block.workbench.blockID ? false : var1.getDistanceSq((double)this.field_20132_h + 0.5D, (double)this.field_20131_i + 0.5D, (double)this.field_20130_j + 0.5D) <= 64.0D;
	}
}
