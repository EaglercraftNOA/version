package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class PlayerControllerSP extends PlayerController {
	private int field_1074_c = -1;
	private int field_1073_d = -1;
	private int field_1072_e = -1;
	private float curBlockDamage = 0.0F;
	private float prevBlockDamage = 0.0F;
	private float field_1069_h = 0.0F;
	private int field_1068_i = 0;

	public PlayerControllerSP(Minecraft var1) {
		super(var1);
	}

	public void flipPlayer(EntityPlayer var1) {
		var1.rotationYaw = -180.0F;
	}

	public boolean sendBlockRemoved(int var1, int var2, int var3, int var4) {
		int var5 = this.mc.theWorld.getBlockId(var1, var2, var3);
		int var6 = this.mc.theWorld.getBlockMetadata(var1, var2, var3);
		boolean var7 = super.sendBlockRemoved(var1, var2, var3, var4);
		ItemStack var8 = this.mc.thePlayer.getCurrentEquippedItem();
		boolean var9 = this.mc.thePlayer.canHarvestBlock(Block.blocksList[var5]);
		if(var8 != null) {
			var8.hitBlock(var5, var1, var2, var3);
			if(var8.stackSize == 0) {
				var8.func_1097_a(this.mc.thePlayer);
				this.mc.thePlayer.destroyCurrentEquippedItem();
			}
		}

		if(var7 && var9) {
			Block.blocksList[var5].harvestBlock(this.mc.theWorld, var1, var2, var3, var6);
		}

		return var7;
	}

	public void clickBlock(int var1, int var2, int var3, int var4) {
		int var5 = this.mc.theWorld.getBlockId(var1, var2, var3);
		if(var5 > 0 && this.curBlockDamage == 0.0F) {
			Block.blocksList[var5].onBlockClicked(this.mc.theWorld, var1, var2, var3, this.mc.thePlayer);
		}

		if(var5 > 0 && Block.blocksList[var5].blockStrength(this.mc.thePlayer) >= 1.0F) {
			this.sendBlockRemoved(var1, var2, var3, var4);
		}

	}

	public void func_6468_a() {
		this.curBlockDamage = 0.0F;
		this.field_1068_i = 0;
	}

	public void sendBlockRemoving(int var1, int var2, int var3, int var4) {
		if(this.field_1068_i > 0) {
			--this.field_1068_i;
		} else {
			if(var1 == this.field_1074_c && var2 == this.field_1073_d && var3 == this.field_1072_e) {
				int var5 = this.mc.theWorld.getBlockId(var1, var2, var3);
				if(var5 == 0) {
					return;
				}

				Block var6 = Block.blocksList[var5];
				this.curBlockDamage += var6.blockStrength(this.mc.thePlayer);
				if(this.field_1069_h % 4.0F == 0.0F && var6 != null) {
					this.mc.sndManager.playSound(var6.stepSound.func_1145_d(), (float)var1 + 0.5F, (float)var2 + 0.5F, (float)var3 + 0.5F, (var6.stepSound.func_1147_b() + 1.0F) / 8.0F, var6.stepSound.func_1144_c() * 0.5F);
				}

				++this.field_1069_h;
				if(this.curBlockDamage >= 1.0F) {
					this.sendBlockRemoved(var1, var2, var3, var4);
					this.curBlockDamage = 0.0F;
					this.prevBlockDamage = 0.0F;
					this.field_1069_h = 0.0F;
					this.field_1068_i = 5;
				}
			} else {
				this.curBlockDamage = 0.0F;
				this.prevBlockDamage = 0.0F;
				this.field_1069_h = 0.0F;
				this.field_1074_c = var1;
				this.field_1073_d = var2;
				this.field_1072_e = var3;
			}

		}
	}

	public void setPartialTime(float var1) {
		if(this.curBlockDamage <= 0.0F) {
			this.mc.ingameGUI.field_6446_b = 0.0F;
			this.mc.renderGlobal.field_1450_i = 0.0F;
		} else {
			float var2 = this.prevBlockDamage + (this.curBlockDamage - this.prevBlockDamage) * var1;
			this.mc.ingameGUI.field_6446_b = var2;
			this.mc.renderGlobal.field_1450_i = var2;
		}

	}

	public float getBlockReachDistance() {
		return 4.0F;
	}

	public void func_717_a(World var1) {
		super.func_717_a(var1);
	}

	public void updateController() {
		this.prevBlockDamage = this.curBlockDamage;
		this.mc.sndManager.func_4033_c();
	}
}
