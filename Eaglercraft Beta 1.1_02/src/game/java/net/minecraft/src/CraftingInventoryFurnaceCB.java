package net.minecraft.src;

public class CraftingInventoryFurnaceCB extends CraftingInventoryCB {
	private TileEntityFurnace field_20127_a;
	private int field_20126_b = 0;
	private int field_20129_c = 0;
	private int field_20128_h = 0;

	public CraftingInventoryFurnaceCB(IInventory var1, TileEntityFurnace var2) {
		this.field_20127_a = var2;
		this.func_20117_a(new Slot(var2, 0, 56, 17));
		this.func_20117_a(new Slot(var2, 1, 56, 53));
		this.func_20117_a(new Slot(var2, 2, 116, 35));

		int var3;
		for(var3 = 0; var3 < 3; ++var3) {
			for(int var4 = 0; var4 < 9; ++var4) {
				this.func_20117_a(new Slot(var1, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
			}
		}

		for(var3 = 0; var3 < 9; ++var3) {
			this.func_20117_a(new Slot(var1, var3, 8 + var3 * 18, 142));
		}

	}

	public void func_20114_a() {
		super.func_20114_a();

		for(int var1 = 0; var1 < this.field_20121_g.size(); ++var1) {
			ICrafting var2 = (ICrafting)this.field_20121_g.get(var1);
			if(this.field_20126_b != this.field_20127_a.furnaceCookTime) {
				var2.func_20158_a(this, 0, this.field_20127_a.furnaceCookTime);
			}

			if(this.field_20129_c != this.field_20127_a.furnaceBurnTime) {
				var2.func_20158_a(this, 1, this.field_20127_a.furnaceBurnTime);
			}

			if(this.field_20128_h != this.field_20127_a.currentItemBurnTime) {
				var2.func_20158_a(this, 2, this.field_20127_a.currentItemBurnTime);
			}
		}

		this.field_20126_b = this.field_20127_a.furnaceCookTime;
		this.field_20129_c = this.field_20127_a.furnaceBurnTime;
		this.field_20128_h = this.field_20127_a.currentItemBurnTime;
	}

	public void func_20112_a(int var1, int var2) {
		if(var1 == 0) {
			this.field_20127_a.furnaceCookTime = var2;
		}

		if(var1 == 1) {
			this.field_20127_a.furnaceBurnTime = var2;
		}

		if(var1 == 2) {
			this.field_20127_a.currentItemBurnTime = var2;
		}

	}

	public boolean func_20120_b(EntityPlayer var1) {
		return this.field_20127_a.func_20070_a_(var1);
	}
}
