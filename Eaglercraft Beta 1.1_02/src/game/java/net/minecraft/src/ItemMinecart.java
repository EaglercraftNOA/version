package net.minecraft.src;

public class ItemMinecart extends Item {
	public int field_317_a;

	public ItemMinecart(int var1, int var2) {
		super(var1);
		this.maxStackSize = 1;
		this.field_317_a = var2;
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7) {
		int var8 = var3.getBlockId(var4, var5, var6);
		if(var8 == Block.minecartTrack.blockID) {
			if(!var3.multiplayerWorld) {
				var3.entityJoinedWorld(new EntityMinecart(var3, (double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), (double)((float)var6 + 0.5F), this.field_317_a));
			}

			--var1.stackSize;
			return true;
		} else {
			return false;
		}
	}
}
