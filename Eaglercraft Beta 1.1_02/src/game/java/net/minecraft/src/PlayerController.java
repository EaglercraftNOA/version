package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class PlayerController {
	protected final Minecraft mc;
	public boolean field_1064_b = false;

	public PlayerController(Minecraft var1) {
		this.mc = var1;
	}

	public void func_717_a(World var1) {
	}

	public void clickBlock(int var1, int var2, int var3, int var4) {
		this.sendBlockRemoved(var1, var2, var3, var4);
	}

	public boolean sendBlockRemoved(int var1, int var2, int var3, int var4) {
		this.mc.effectRenderer.func_1186_a(var1, var2, var3);
		World var5 = this.mc.theWorld;
		Block var6 = Block.blocksList[var5.getBlockId(var1, var2, var3)];
		int var7 = var5.getBlockMetadata(var1, var2, var3);
		boolean var8 = var5.setBlockWithNotify(var1, var2, var3, 0);
		if(var6 != null && var8) {
			this.mc.sndManager.playSound(var6.stepSound.func_1146_a(), (float)var1 + 0.5F, (float)var2 + 0.5F, (float)var3 + 0.5F, (var6.stepSound.func_1147_b() + 1.0F) / 2.0F, var6.stepSound.func_1144_c() * 0.8F);
			var6.onBlockDestroyedByPlayer(var5, var1, var2, var3, var7);
		}

		return var8;
	}

	public void sendBlockRemoving(int var1, int var2, int var3, int var4) {
	}

	public void func_6468_a() {
	}

	public void setPartialTime(float var1) {
	}

	public float getBlockReachDistance() {
		return 5.0F;
	}

	public boolean sendUseItem(EntityPlayer var1, World var2, ItemStack var3) {
		int var4 = var3.stackSize;
		ItemStack var5 = var3.useItemRightClick(var2, var1);
		if(var5 != var3 || var5 != null && var5.stackSize != var4) {
			var1.inventory.mainInventory[var1.inventory.currentItem] = var5;
			if(var5.stackSize == 0) {
				var1.inventory.mainInventory[var1.inventory.currentItem] = null;
			}

			return true;
		} else {
			return false;
		}
	}

	public void flipPlayer(EntityPlayer var1) {
	}

	public void updateController() {
	}

	public boolean shouldDrawHUD() {
		return true;
	}

	public void func_6473_b(EntityPlayer var1) {
	}

	public boolean sendPlaceBlock(EntityPlayer var1, World var2, ItemStack var3, int var4, int var5, int var6, int var7) {
		int var8 = var2.getBlockId(var4, var5, var6);
		return var8 > 0 && Block.blocksList[var8].blockActivated(var2, var4, var5, var6, var1) ? true : (var3 == null ? false : var3.useItem(var1, var2, var4, var5, var6, var7));
	}

	public EntityPlayer func_4087_b(World var1) {
		return new EntityPlayerSP(this.mc, var1, this.mc.session, var1.worldProvider.worldType);
	}

	public void func_6475_a(EntityPlayer var1, Entity var2) {
		var1.useCurrentItemOnEntity(var2);
	}

	public void func_6472_b(EntityPlayer var1, Entity var2) {
		var1.attackTargetEntityWithCurrentItem(var2);
	}

	public ItemStack func_20085_a(int var1, int var2, int var3, EntityPlayer var4) {
		return var4.field_20068_h.func_20116_a(var2, var3, var4);
	}

	public void func_20086_a(int var1, EntityPlayer var2) {
		var2.field_20068_h.onCraftGuiClosed(var2);
		var2.field_20068_h = var2.field_20069_g;
	}
}
