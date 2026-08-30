package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class PlayerControllerMP extends PlayerController {
	private int field_9445_c = -1;
	private int field_9444_d = -1;
	private int field_9443_e = -1;
	private float field_9442_f = 0.0F;
	private float field_1080_g = 0.0F;
	private float field_9441_h = 0.0F;
	private int field_9440_i = 0;
	private boolean field_9439_j = false;
	private NetClientHandler field_9438_k;
	private int field_1075_l = 0;

	public PlayerControllerMP(Minecraft var1, NetClientHandler var2) {
		super(var1);
		this.field_9438_k = var2;
	}

	public void flipPlayer(EntityPlayer var1) {
		var1.rotationYaw = -180.0F;
	}

	public boolean sendBlockRemoved(int var1, int var2, int var3, int var4) {
		this.field_9438_k.addToSendQueue(new Packet14BlockDig(3, var1, var2, var3, var4));
		int var5 = this.mc.theWorld.getBlockId(var1, var2, var3);
		boolean var6 = super.sendBlockRemoved(var1, var2, var3, var4);
		ItemStack var7 = this.mc.thePlayer.getCurrentEquippedItem();
		if(var7 != null) {
			var7.hitBlock(var5, var1, var2, var3);
			if(var7.stackSize == 0) {
				var7.func_1097_a(this.mc.thePlayer);
				this.mc.thePlayer.destroyCurrentEquippedItem();
			}
		}

		return var6;
	}

	public void clickBlock(int var1, int var2, int var3, int var4) {
		this.field_9439_j = true;
		this.field_9438_k.addToSendQueue(new Packet14BlockDig(0, var1, var2, var3, var4));
		int var5 = this.mc.theWorld.getBlockId(var1, var2, var3);
		if(var5 > 0 && this.field_9442_f == 0.0F) {
			Block.blocksList[var5].onBlockClicked(this.mc.theWorld, var1, var2, var3, this.mc.thePlayer);
		}

		if(var5 > 0 && Block.blocksList[var5].blockStrength(this.mc.thePlayer) >= 1.0F) {
			this.sendBlockRemoved(var1, var2, var3, var4);
		}

	}

	public void func_6468_a() {
		if(this.field_9439_j) {
			this.field_9439_j = false;
			this.field_9438_k.addToSendQueue(new Packet14BlockDig(2, 0, 0, 0, 0));
			this.field_9442_f = 0.0F;
			this.field_9440_i = 0;
		}
	}

	public void sendBlockRemoving(int var1, int var2, int var3, int var4) {
		this.field_9439_j = true;
		this.func_730_e();
		this.field_9438_k.addToSendQueue(new Packet14BlockDig(1, var1, var2, var3, var4));
		if(this.field_9440_i > 0) {
			--this.field_9440_i;
		} else {
			if(var1 == this.field_9445_c && var2 == this.field_9444_d && var3 == this.field_9443_e) {
				int var5 = this.mc.theWorld.getBlockId(var1, var2, var3);
				if(var5 == 0) {
					return;
				}

				Block var6 = Block.blocksList[var5];
				this.field_9442_f += var6.blockStrength(this.mc.thePlayer);
				if(this.field_9441_h % 4.0F == 0.0F && var6 != null) {
					this.mc.sndManager.playSound(var6.stepSound.func_1145_d(), (float)var1 + 0.5F, (float)var2 + 0.5F, (float)var3 + 0.5F, (var6.stepSound.func_1147_b() + 1.0F) / 8.0F, var6.stepSound.func_1144_c() * 0.5F);
				}

				++this.field_9441_h;
				if(this.field_9442_f >= 1.0F) {
					this.sendBlockRemoved(var1, var2, var3, var4);
					this.field_9442_f = 0.0F;
					this.field_1080_g = 0.0F;
					this.field_9441_h = 0.0F;
					this.field_9440_i = 5;
				}
			} else {
				this.field_9442_f = 0.0F;
				this.field_1080_g = 0.0F;
				this.field_9441_h = 0.0F;
				this.field_9445_c = var1;
				this.field_9444_d = var2;
				this.field_9443_e = var3;
			}

		}
	}

	public void setPartialTime(float var1) {
		if(this.field_9442_f <= 0.0F) {
			this.mc.ingameGUI.field_6446_b = 0.0F;
			this.mc.renderGlobal.field_1450_i = 0.0F;
		} else {
			float var2 = this.field_1080_g + (this.field_9442_f - this.field_1080_g) * var1;
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
		this.func_730_e();
		this.field_1080_g = this.field_9442_f;
		this.mc.sndManager.func_4033_c();
	}

	private void func_730_e() {
		int var1 = this.mc.thePlayer.inventory.currentItem;
		if(var1 != this.field_1075_l) {
			this.field_1075_l = var1;
			this.field_9438_k.addToSendQueue(new Packet16BlockItemSwitch(this.field_1075_l));
		}

	}

	public boolean sendPlaceBlock(EntityPlayer var1, World var2, ItemStack var3, int var4, int var5, int var6, int var7) {
		this.func_730_e();
		boolean var8 = super.sendPlaceBlock(var1, var2, var3, var4, var5, var6, var7);
		this.field_9438_k.addToSendQueue(new Packet15Place(var4, var5, var6, var7, var1.inventory.getCurrentItem()));
		return var8;
	}

	public boolean sendUseItem(EntityPlayer var1, World var2, ItemStack var3) {
		this.func_730_e();
		boolean var4 = super.sendUseItem(var1, var2, var3);
		this.field_9438_k.addToSendQueue(new Packet15Place(-1, -1, -1, 255, var1.inventory.getCurrentItem()));
		return var4;
	}

	public EntityPlayer func_4087_b(World var1) {
		return new EntityClientPlayerMP(this.mc, var1, this.mc.session, this.field_9438_k);
	}

	public void func_6472_b(EntityPlayer var1, Entity var2) {
		this.func_730_e();
		this.field_9438_k.addToSendQueue(new Packet7(var1.field_620_ab, var2.field_620_ab, 1));
		var1.attackTargetEntityWithCurrentItem(var2);
	}

	public void func_6475_a(EntityPlayer var1, Entity var2) {
		this.func_730_e();
		this.field_9438_k.addToSendQueue(new Packet7(var1.field_620_ab, var2.field_620_ab, 0));
		var1.useCurrentItemOnEntity(var2);
	}

	public ItemStack func_20085_a(int var1, int var2, int var3, EntityPlayer var4) {
		short var5 = var4.field_20068_h.func_20111_a(var4.inventory);
		ItemStack var6 = super.func_20085_a(var1, var2, var3, var4);
		this.field_9438_k.addToSendQueue(new Packet102(var1, var2, var3, var6, var5));
		return var6;
	}

	public void func_20086_a(int var1, EntityPlayer var2) {
		if(var1 != -9999) {
		}
	}
}
