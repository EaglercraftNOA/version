package net.minecraft.src;

import net.lax1dude.eaglercraft.Random;
import net.minecraft.client.Minecraft;

public class NetClientHandler extends NetHandler {
	private boolean disconnected = false;
	NetworkManager netManager;
	public String field_1209_a;
	private Minecraft mc;
	private WorldClient worldClient;
	private boolean field_1210_g = false;
	Random rand = new Random();

	public NetClientHandler(Minecraft var1) {
		this.mc = var1;
		this.netManager = new NetworkManager(this);
	}

	public void processReadPackets() {
		if(!this.disconnected) {
			this.netManager.readPacket();
			this.netManager.processReadPackets();
		}
	}

	public void handleLogin(Packet1Login var1) {
		this.mc.playerController = new PlayerControllerMP(this.mc, this);
		this.worldClient = new WorldClient(this, var1.field_4074_d, var1.field_4073_e);
		this.worldClient.multiplayerWorld = true;
		this.mc.func_6261_a(this.worldClient);
		this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
		this.mc.thePlayer.field_620_ab = var1.protocolVersion;
	}

	public void handlePickupSpawn(Packet21PickupSpawn var1) {
		double var2 = (double)var1.xPosition / 32.0D;
		double var4 = (double)var1.yPosition / 32.0D;
		double var6 = (double)var1.zPosition / 32.0D;
		EntityItem var8 = new EntityItem(this.worldClient, var2, var4, var6, new ItemStack(var1.itemId, var1.count));
		var8.motionX = (double)var1.rotation / 128.0D;
		var8.motionY = (double)var1.pitch / 128.0D;
		var8.motionZ = (double)var1.roll / 128.0D;
		var8.serverPosX = var1.xPosition;
		var8.serverPosY = var1.yPosition;
		var8.serverPosZ = var1.zPosition;
		this.worldClient.func_712_a(var1.entityId, var8);
	}

	public void handleVehicleSpawn(Packet23VehicleSpawn var1) {
		double var2 = (double)var1.xPosition / 32.0D;
		double var4 = (double)var1.yPosition / 32.0D;
		double var6 = (double)var1.zPosition / 32.0D;
		Object var8 = null;
		if(var1.type == 10) {
			var8 = new EntityMinecart(this.worldClient, var2, var4, var6, 0);
		}

		if(var1.type == 11) {
			var8 = new EntityMinecart(this.worldClient, var2, var4, var6, 1);
		}

		if(var1.type == 12) {
			var8 = new EntityMinecart(this.worldClient, var2, var4, var6, 2);
		}

		if(var1.type == 90) {
			var8 = new EntityFish(this.worldClient, var2, var4, var6);
		}

		if(var1.type == 60) {
			var8 = new EntityArrow(this.worldClient, var2, var4, var6);
		}

		if(var1.type == 61) {
			var8 = new EntitySnowball(this.worldClient, var2, var4, var6);
		}

		if(var1.type == 62) {
			var8 = new EntityEgg(this.worldClient, var2, var4, var6);
		}

		if(var1.type == 1) {
			var8 = new EntityBoat(this.worldClient, var2, var4, var6);
		}

		if(var1.type == 50) {
			var8 = new EntityTNTPrimed(this.worldClient, var2, var4, var6);
		}

		if(var1.type == 70) {
			var8 = new EntityFallingSand(this.worldClient, var2, var4, var6, Block.sand.blockID);
		}

		if(var1.type == 71) {
			var8 = new EntityFallingSand(this.worldClient, var2, var4, var6, Block.gravel.blockID);
		}

		if(var8 != null) {
			((Entity)var8).serverPosX = var1.xPosition;
			((Entity)var8).serverPosY = var1.yPosition;
			((Entity)var8).serverPosZ = var1.zPosition;
			((Entity)var8).rotationYaw = 0.0F;
			((Entity)var8).rotationPitch = 0.0F;
			((Entity)var8).field_620_ab = var1.entityId;
			this.worldClient.func_712_a(var1.entityId, (Entity)var8);
		}

	}

	public void func_6498_a(Packet28 var1) {
		Entity var2 = this.func_12246_a(var1.field_6367_a);
		if(var2 != null) {
			var2.setVelocity((double)var1.field_6366_b / 8000.0D, (double)var1.field_6369_c / 8000.0D, (double)var1.field_6368_d / 8000.0D);
		}
	}

	public void handleNamedEntitySpawn(Packet20NamedEntitySpawn var1) {
		double var2 = (double)var1.xPosition / 32.0D;
		double var4 = (double)var1.yPosition / 32.0D;
		double var6 = (double)var1.zPosition / 32.0D;
		float var8 = (float)(var1.rotation * 360) / 256.0F;
		float var9 = (float)(var1.pitch * 360) / 256.0F;
		EntityOtherPlayerMP var10 = new EntityOtherPlayerMP(this.mc.theWorld, var1.name);
		var10.serverPosX = var1.xPosition;
		var10.serverPosY = var1.yPosition;
		var10.serverPosZ = var1.zPosition;
		int var11 = var1.currentItem;
		if(var11 == 0) {
			var10.inventory.mainInventory[var10.inventory.currentItem] = null;
		} else {
			var10.inventory.mainInventory[var10.inventory.currentItem] = new ItemStack(var11);
		}

		var10.setPositionAndRotation(var2, var4, var6, var8, var9);
		this.worldClient.func_712_a(var1.entityId, var10);
	}

	public void handleEntityTeleport(Packet34EntityTeleport var1) {
		Entity var2 = this.func_12246_a(var1.entityId);
		if(var2 != null) {
			var2.serverPosX = var1.xPosition;
			var2.serverPosY = var1.yPosition;
			var2.serverPosZ = var1.zPosition;
			double var3 = (double)var2.serverPosX / 32.0D;
			double var5 = (double)var2.serverPosY / 32.0D + 1.0D / 64.0D;
			double var7 = (double)var2.serverPosZ / 32.0D;
			float var9 = (float)(var1.yaw * 360) / 256.0F;
			float var10 = (float)(var1.pitch * 360) / 256.0F;
			var2.setPositionAndRotation2(var3, var5, var7, var9, var10, 3);
		}
	}

	public void handleEntity(Packet30Entity var1) {
		Entity var2 = this.func_12246_a(var1.entityId);
		if(var2 != null) {
			var2.serverPosX += var1.xPosition;
			var2.serverPosY += var1.yPosition;
			var2.serverPosZ += var1.zPosition;
			double var3 = (double)var2.serverPosX / 32.0D;
			double var5 = (double)var2.serverPosY / 32.0D + 1.0D / 64.0D;
			double var7 = (double)var2.serverPosZ / 32.0D;
			float var9 = var1.rotating ? (float)(var1.yaw * 360) / 256.0F : var2.rotationYaw;
			float var10 = var1.rotating ? (float)(var1.pitch * 360) / 256.0F : var2.rotationPitch;
			var2.setPositionAndRotation2(var3, var5, var7, var9, var10, 3);
		}
	}

	public void handleDestroyEntity(Packet29DestroyEntity var1) {
		this.worldClient.removeEntityFromWorld(var1.entityId);
	}

	public void handleFlying(Packet10Flying var1) {
		EntityPlayerSP var2 = this.mc.thePlayer;
		double var3 = var2.posX;
		double var5 = var2.posY;
		double var7 = var2.posZ;
		float var9 = var2.rotationYaw;
		float var10 = var2.rotationPitch;
		if(var1.moving) {
			var3 = var1.xPosition;
			var5 = var1.yPosition;
			var7 = var1.zPosition;
		}

		if(var1.rotating) {
			var9 = var1.yaw;
			var10 = var1.pitch;
		}

		var2.field_9287_aY = 0.0F;
		var2.motionX = var2.motionY = var2.motionZ = 0.0D;
		var2.setPositionAndRotation(var3, var5, var7, var9, var10);
		var1.xPosition = var2.posX;
		var1.yPosition = var2.boundingBox.minY;
		var1.zPosition = var2.posZ;
		var1.stance = var2.posY;
		this.netManager.addToSendQueue(var1);
		if(!this.field_1210_g) {
			this.mc.thePlayer.prevPosX = this.mc.thePlayer.posX;
			this.mc.thePlayer.prevPosY = this.mc.thePlayer.posY;
			this.mc.thePlayer.prevPosZ = this.mc.thePlayer.posZ;
			this.field_1210_g = true;
			this.mc.displayGuiScreen((GuiScreen)null);
		}

	}

	public void handlePreChunk(Packet50PreChunk var1) {
		this.worldClient.func_713_a(var1.xPosition, var1.yPosition, var1.mode);
	}

	public void handleMultiBlockChange(Packet52MultiBlockChange var1) {
		Chunk var2 = this.worldClient.getChunkFromChunkCoords(var1.xPosition, var1.zPosition);
		int var3 = var1.xPosition * 16;
		int var4 = var1.zPosition * 16;

		for(int var5 = 0; var5 < var1.size; ++var5) {
			short var6 = var1.coordinateArray[var5];
			int var7 = var1.typeArray[var5] & 255;
			byte var8 = var1.metadataArray[var5];
			int var9 = var6 >> 12 & 15;
			int var10 = var6 >> 8 & 15;
			int var11 = var6 & 255;
			var2.setBlockIDWithMetadata(var9, var11, var10, var7, var8);
			this.worldClient.func_711_c(var9 + var3, var11, var10 + var4, var9 + var3, var11, var10 + var4);
			this.worldClient.func_701_b(var9 + var3, var11, var10 + var4, var9 + var3, var11, var10 + var4);
		}

	}

	public void handleMapChunk(Packet51MapChunk var1) {
		this.worldClient.func_711_c(var1.xPosition, var1.yPosition, var1.zPosition, var1.xPosition + var1.xSize - 1, var1.yPosition + var1.ySize - 1, var1.zPosition + var1.zSize - 1);
		this.worldClient.func_693_a(var1.xPosition, var1.yPosition, var1.zPosition, var1.xSize, var1.ySize, var1.zSize, var1.chunk);
	}

	public void handleBlockChange(Packet53BlockChange var1) {
		this.worldClient.func_714_c(var1.xPosition, var1.yPosition, var1.zPosition, var1.type, var1.metadata);
	}

	public void handleKickDisconnect(Packet255KickDisconnect var1) {
		this.netManager.networkShutdown("disconnect.kicked", new Object[0]);
		this.disconnected = true;
		this.mc.func_6261_a((World)null);
		this.mc.displayGuiScreen(new GuiConnectFailed("disconnect.disconnected", "disconnect.genericReason", new Object[]{var1.reason}));
	}

	public void handleErrorMessage(String var1, Object[] var2) {
		if(!this.disconnected) {
			this.disconnected = true;
			this.mc.func_6261_a((World)null);
			this.mc.displayGuiScreen(new GuiConnectFailed("disconnect.lost", var1, var2));
		}
	}

	public void addToSendQueue(Packet var1) {
		if(!this.disconnected) {
			this.netManager.addToSendQueue(var1);
		}
	}

	public void handleCollect(Packet22Collect var1) {
		Entity var2 = this.func_12246_a(var1.collectedEntityId);
		Object var3 = (EntityLiving)this.func_12246_a(var1.collectorEntityId);
		if(var3 == null) {
			var3 = this.mc.thePlayer;
		}

		if(var2 != null) {
			this.worldClient.playSoundAtEntity(var2, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			this.mc.effectRenderer.func_1192_a(new EntityPickupFX(this.mc.theWorld, var2, (Entity)var3, -0.5F));
			this.worldClient.removeEntityFromWorld(var1.collectedEntityId);
		}

	}

	public void handleChat(Packet3Chat var1) {
		this.mc.ingameGUI.addChatMessage(var1.message);
	}

	public void handleArmAnimation(Packet18ArmAnimation var1) {
		Entity var2 = this.func_12246_a(var1.entityId);
		if(var2 != null) {
			if(var1.animate == 1) {
				EntityPlayer var3 = (EntityPlayer)var2;
				var3.swingItem();
			} else if(var1.animate == 100) {
				var2.field_9300_bu = true;
			} else if(var1.animate == 101) {
				var2.field_9300_bu = false;
			} else if(var1.animate == 102) {
				var2.field_9299_bv = true;
			} else if(var1.animate == 103) {
				var2.field_9299_bv = false;
			} else if(var1.animate == 104) {
				var2.field_12240_bw = true;
			} else if(var1.animate == 105) {
				var2.field_12240_bw = false;
			} else if(var1.animate == 2) {
				var2.performHurtAnimation();
			}

		}
	}

	public void handleHandshake(Packet2Handshake var1) {
		this.addToSendQueue(new Packet1Login(this.mc.session.playerName, "Password", 8));
	}

	public void disconnect() {
		this.disconnected = true;
		this.netManager.networkShutdown("disconnect.closed", new Object[0]);
	}

	public void handleMobSpawn(Packet24MobSpawn var1) {
		double var2 = (double)var1.xPosition / 32.0D;
		double var4 = (double)var1.yPosition / 32.0D;
		double var6 = (double)var1.zPosition / 32.0D;
		float var8 = (float)(var1.yaw * 360) / 256.0F;
		float var9 = (float)(var1.pitch * 360) / 256.0F;
		EntityLiving var10 = (EntityLiving)EntityList.createEntity(var1.type, this.mc.theWorld);
		var10.serverPosX = var1.xPosition;
		var10.serverPosY = var1.yPosition;
		var10.serverPosZ = var1.zPosition;
		var10.field_620_ab = var1.entityId;
		var10.setPositionAndRotation(var2, var4, var6, var8, var9);
		var10.field_9343_G = true;
		this.worldClient.func_712_a(var1.entityId, var10);
	}

	public void handleUpdateTime(Packet4UpdateTime var1) {
		this.mc.theWorld.setWorldTime(var1.time);
	}

	public void handleSpawnPosition(Packet6SpawnPosition var1) {
		this.worldClient.spawnX = var1.xPosition;
		this.worldClient.spawnY = var1.yPosition;
		this.worldClient.spawnZ = var1.zPosition;
	}

	public void func_6497_a(Packet39 var1) {
		Object var2 = this.func_12246_a(var1.field_6365_a);
		Entity var3 = this.func_12246_a(var1.field_6364_b);
		if(var1.field_6365_a == this.mc.thePlayer.field_620_ab) {
			var2 = this.mc.thePlayer;
		}

		if(var2 != null) {
			((Entity)var2).mountEntity(var3);
		}
	}

	public void func_9447_a(Packet38 var1) {
		Entity var2 = this.func_12246_a(var1.field_9274_a);
		if(var2 != null) {
			var2.func_9282_a(var1.field_9273_b);
		}

	}

	private Entity func_12246_a(int var1) {
		return (Entity)(var1 == this.mc.thePlayer.field_620_ab ? this.mc.thePlayer : this.worldClient.func_709_b(var1));
	}

	public void handleHealth(Packet8 var1) {
		this.mc.thePlayer.setHealth(var1.healthMP);
	}

	public void func_9448_a(Packet9 var1) {
		this.mc.respawn();
	}

	public void func_12245_a(Packet60 var1) {
		Explosion var2 = new Explosion(this.mc.theWorld, (Entity)null, var1.field_12236_a, var1.field_12235_b, var1.field_12239_c, var1.field_12238_d);
		var2.field_12251_g = var1.field_12237_e;
		var2.func_12247_b();
	}

	public void func_20087_a(Packet100 var1) {
		if(var1.field_20037_b == 0) {
			InventoryBasic var2 = new InventoryBasic(var1.field_20040_c, var1.field_20039_d);
			this.mc.thePlayer.displayGUIChest(var2);
			this.mc.thePlayer.field_20068_h.unusedList = var1.field_20038_a;
		} else if(var1.field_20037_b == 2) {
			TileEntityFurnace var3 = new TileEntityFurnace();
			this.mc.thePlayer.displayGUIFurnace(var3);
			this.mc.thePlayer.field_20068_h.unusedList = var1.field_20038_a;
		} else if(var1.field_20037_b == 1) {
			EntityPlayerSP var4 = this.mc.thePlayer;
			this.mc.thePlayer.displayWorkbenchGUI(MathHelper.floor_double(var4.posX), MathHelper.floor_double(var4.posY), MathHelper.floor_double(var4.posZ));
			this.mc.thePlayer.field_20068_h.unusedList = var1.field_20038_a;
		}

	}

	public void func_20088_a(Packet103 var1) {
		if(var1.field_20042_a == -1) {
			this.mc.thePlayer.inventory.func_20076_b(var1.field_20043_c);
		} else if(var1.field_20042_a == 0) {
			this.mc.thePlayer.field_20069_g.func_20119_a(var1.field_20041_b, var1.field_20043_c);
		} else if(var1.field_20042_a == this.mc.thePlayer.field_20068_h.unusedList) {
			this.mc.thePlayer.field_20068_h.func_20119_a(var1.field_20041_b, var1.field_20043_c);
		}

	}

	public void func_20089_a(Packet106 var1) {
		CraftingInventoryCB var2 = null;
		if(var1.field_20029_a == 0) {
			var2 = this.mc.thePlayer.field_20069_g;
		} else if(var1.field_20029_a == this.mc.thePlayer.field_20068_h.unusedList) {
			var2 = this.mc.thePlayer.field_20068_h;
		}

		if(var2 != null) {
			if(var1.field_20030_c) {
				var2.func_20113_a(var1.field_20028_b);
			} else {
				var2.func_20110_b(var1.field_20028_b);
				this.addToSendQueue(new Packet106(var1.field_20029_a, var1.field_20028_b, true));
			}
		}

	}

	public void func_20094_a(Packet104 var1) {
		if(var1.field_20036_a == 0) {
			this.mc.thePlayer.field_20069_g.func_20115_a(var1.field_20035_b);
		} else if(var1.field_20036_a == this.mc.thePlayer.field_20068_h.unusedList) {
			this.mc.thePlayer.field_20068_h.func_20115_a(var1.field_20035_b);
		}

	}

	public void func_20093_a(Packet130 var1) {
		if(this.mc.theWorld.blockExists(var1.field_20020_a, var1.field_20019_b, var1.field_20022_c)) {
			TileEntity var2 = this.mc.theWorld.getBlockTileEntity(var1.field_20020_a, var1.field_20019_b, var1.field_20022_c);
			if(var2 instanceof TileEntitySign) {
				TileEntitySign var3 = (TileEntitySign)var2;

				for(int var4 = 0; var4 < 4; ++var4) {
					var3.signText[var4] = var1.field_20021_d[var4];
				}

				var3.onInventoryChanged();
			}
		}

	}

	public void func_20090_a(Packet105 var1) {
		this.func_4114_b(var1);
		if(this.mc.thePlayer.field_20068_h != null && this.mc.thePlayer.field_20068_h.unusedList == var1.field_20032_a) {
			this.mc.thePlayer.field_20068_h.func_20112_a(var1.field_20031_b, var1.field_20033_c);
		}

	}

	public void handlePlayerInventory(Packet5PlayerInventory var1) {
		Entity var2 = this.func_12246_a(var1.type);
		if(var2 != null) {
			var2.func_20045_c(var1.stacks, var1.field_20044_c);
		}

	}

	public void func_20092_a(Packet101 var1) {
		this.mc.thePlayer.func_20059_m();
	}
}
