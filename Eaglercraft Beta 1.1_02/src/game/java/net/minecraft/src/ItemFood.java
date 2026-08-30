package net.minecraft.src;

public class ItemFood extends Item {
	private int a;

	public ItemFood(int var1, int var2) {
		super(var1);
		this.a = var2;
		this.maxStackSize = 1;
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		--var1.stackSize;
		var3.heal(this.a);
		return var1;
	}
}
