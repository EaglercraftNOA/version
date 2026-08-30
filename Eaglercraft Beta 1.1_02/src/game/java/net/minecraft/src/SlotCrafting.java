package net.minecraft.src;

public class SlotCrafting extends Slot {
	private final IInventory craftMatrix;

	public SlotCrafting(IInventory var1, IInventory var2, int var3, int var4, int var5) {
		super(var2, var3, var4, var5);
		this.craftMatrix = var1;
	}

	public boolean isItemValid(ItemStack var1) {
		return false;
	}

	public void onPickupFromSlot() {
		for(int var1 = 0; var1 < this.craftMatrix.getSizeInventory(); ++var1) {
			if(this.craftMatrix.getStackInSlot(var1) != null) {
				this.craftMatrix.decrStackSize(var1, 1);
			}
		}

	}
}
