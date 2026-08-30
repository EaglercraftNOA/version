package net.minecraft.src;

public class InventoryCrafting implements IInventory {
	private ItemStack[] stackList;
	private int nbrSlots;
	private CraftingInventoryCB eventHandler;

	public InventoryCrafting(CraftingInventoryCB var1, int var2, int var3) {
		this.nbrSlots = var2 * var3;
		this.stackList = new ItemStack[this.nbrSlots];
		this.eventHandler = var1;
	}

	public int getSizeInventory() {
		return this.nbrSlots;
	}

	public ItemStack getStackInSlot(int var1) {
		return this.stackList[var1];
	}

	public String getInvName() {
		return "Crafting";
	}

	public ItemStack decrStackSize(int var1, int var2) {
		if(this.stackList[var1] != null) {
			ItemStack var3;
			if(this.stackList[var1].stackSize <= var2) {
				var3 = this.stackList[var1];
				this.stackList[var1] = null;
				this.eventHandler.onCraftMatrixChanged(this);
				return var3;
			} else {
				var3 = this.stackList[var1].splitStack(var2);
				if(this.stackList[var1].stackSize == 0) {
					this.stackList[var1] = null;
				}

				this.eventHandler.onCraftMatrixChanged(this);
				return var3;
			}
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int var1, ItemStack var2) {
		this.stackList[var1] = var2;
		this.eventHandler.onCraftMatrixChanged(this);
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void onInventoryChanged() {
	}

	public boolean func_20070_a_(EntityPlayer var1) {
		return true;
	}
}
