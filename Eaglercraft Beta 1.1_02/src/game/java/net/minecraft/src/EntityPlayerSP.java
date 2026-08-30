package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class EntityPlayerSP extends EntityPlayer {
	public MovementInput movementInput;
	protected Minecraft mc;
	public int field_9373_b = 20;
	private boolean inPortal = false;
	public float timeInPortal;
	public float prevTimeInPortal;

	public EntityPlayerSP(Minecraft var1, World var2, Session var3, int var4) {
		super(var2);
		this.mc = var1;
		this.dimension = var4;
		this.field_771_i = var3.playerName;
	}

	public void updatePlayerActionState() {
		super.updatePlayerActionState();
		this.moveStrafing = this.movementInput.moveStrafe;
		this.moveForward = this.movementInput.moveForward;
		this.isJumping = this.movementInput.jump;
	}

	public void onLivingUpdate() {
		this.prevTimeInPortal = this.timeInPortal;
		if(this.inPortal) {
			if(this.timeInPortal == 0.0F) {
				this.mc.sndManager.func_337_a("portal.trigger", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
			}

			this.timeInPortal += 0.0125F;
			if(this.timeInPortal >= 1.0F) {
				this.timeInPortal = 1.0F;
				this.field_9373_b = 10;
				this.mc.sndManager.func_337_a("portal.travel", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
				this.mc.usePortal();
			}

			this.inPortal = false;
		} else {
			if(this.timeInPortal > 0.0F) {
				this.timeInPortal -= 0.05F;
			}

			if(this.timeInPortal < 0.0F) {
				this.timeInPortal = 0.0F;
			}
		}

		if(this.field_9373_b > 0) {
			--this.field_9373_b;
		}

		this.movementInput.updatePlayerMoveState(this);
		if(this.movementInput.sneak && this.field_9287_aY < 0.2F) {
			this.field_9287_aY = 0.2F;
		}

		super.onLivingUpdate();
	}

	public void resetPlayerKeyState() {
		this.movementInput.resetKeyState();
	}

	public void handleKeyPress(int var1, boolean var2) {
		this.movementInput.checkKeyForMovementInput(var1, var2);
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setInteger("Score", this.score);
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.score = var1.getInteger("Score");
	}

	public void func_20059_m() {
		super.func_20059_m();
		this.mc.displayGuiScreen((GuiScreen)null);
	}

	public void displayGUIEditSign(TileEntitySign var1) {
		this.mc.displayGuiScreen(new GuiEditSign(var1));
	}

	public void displayGUIChest(IInventory var1) {
		this.mc.displayGuiScreen(new GuiChest(this.inventory, var1));
	}

	public void displayWorkbenchGUI(int var1, int var2, int var3) {
		this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj, var1, var2, var3));
	}

	public void displayGUIFurnace(TileEntityFurnace var1) {
		this.mc.displayGuiScreen(new GuiFurnace(this.inventory, var1));
	}

	public void onItemPickup(Entity var1, int var2) {
		this.mc.effectRenderer.func_1192_a(new EntityPickupFX(this.mc.theWorld, var1, this, -0.5F));
	}

	public int getPlayerArmorValue() {
		return this.inventory.getTotalArmorValue();
	}

	public void useCurrentItemOnEntity(Entity var1) {
		if(!var1.interact(this)) {
			ItemStack var2 = this.getCurrentEquippedItem();
			if(var2 != null && var1 instanceof EntityLiving) {
				var2.useItemOnEntity((EntityLiving)var1);
				if(var2.stackSize <= 0) {
					var2.func_1097_a(this);
					this.destroyCurrentEquippedItem();
				}
			}

		}
	}

	public void sendChatMessage(String var1) {
	}

	public void func_6420_o() {
	}

	public boolean isSneaking() {
		return this.movementInput.sneak;
	}

	public void setInPortal() {
		if(this.field_9373_b > 0) {
			this.field_9373_b = 10;
		} else {
			this.inPortal = true;
		}
	}

	public void setHealth(int var1) {
		int var2 = this.health - var1;
		if(var2 <= 0) {
			this.health = var1;
		} else {
			this.field_9346_af = var2;
			this.prevHealth = this.health;
			this.field_9306_bj = this.field_9366_o;
			this.damageEntity(var2);
			this.hurtTime = this.maxHurtTime = 10;
		}

	}

	public void respawnPlayer() {
		this.mc.respawn();
	}
}
