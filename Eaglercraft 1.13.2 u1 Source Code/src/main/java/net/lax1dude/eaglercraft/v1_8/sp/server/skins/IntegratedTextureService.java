/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.sp.server.skins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import net.lax1dude.eaglercraft.v1_8.Base64;
import net.lax1dude.eaglercraft.v1_8.EaglerUUIDHelper;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapePresetEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapePresetV5EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinPresetEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinPresetV5EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherTexturesV5EAG;
import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerPlayerList;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.player.EntityPlayerMP;

public class IntegratedTextureService {

	private final EaglerPlayerList playerList;
	private final CustomSkullLoader skullHandler;

	public IntegratedTextureService(EaglerPlayerList playerList, VFile2 file) {
		this.playerList = playerList;
		this.skullHandler = new CustomSkullLoader(file);
	}

	private static UUID toJavaUUID(EaglercraftUUID uuid) {
		return EaglerUUIDHelper.fromBits(uuid.msb, uuid.lsb);
	}

	public void handleRequestPlayerSkin(EntityPlayerMP requester, EaglercraftUUID uuid) {
		EntityPlayerMP target = playerList.getPlayer(toJavaUUID(uuid));
		if (target != null && target.textureData != null) {
			requester.connection.sendEaglerMessage(target.textureData.getSkin(uuid.msb, uuid.lsb,
					requester.connection.getEaglerMessageProtocol()));
		} else {
			requester.connection.sendEaglerMessage(
					new SPacketOtherSkinPresetEAG(uuid.msb, uuid.lsb, (uuid.hashCode() & 1) != 0 ? 1 : 0));
		}
	}

	public void handleRequestPlayerCape(EntityPlayerMP requester, EaglercraftUUID uuid) {
		EntityPlayerMP target = playerList.getPlayer(toJavaUUID(uuid));
		if (target != null && target.textureData != null) {
			requester.connection.sendEaglerMessage(target.textureData.getCape(uuid.msb, uuid.lsb,
					requester.connection.getEaglerMessageProtocol()));
		} else {
			requester.connection.sendEaglerMessage(new SPacketOtherCapePresetEAG(uuid.msb, uuid.lsb, 0));
		}
	}

	public void handleRequestPlayerSkinV5(EntityPlayerMP requester, int requestId, EaglercraftUUID uuid) {
		EntityPlayerMP target = playerList.getPlayer(toJavaUUID(uuid));
		if (target != null && target.textureData != null) {
			requester.connection.sendEaglerMessage(target.textureData.getSkinV5(requestId,
					requester.connection.getEaglerMessageProtocol()));
		} else {
			requester.connection.sendEaglerMessage(
					new SPacketOtherSkinPresetV5EAG(requestId, (uuid.hashCode() & 1) != 0 ? 1 : 0));
		}
	}

	public void handleRequestPlayerCapeV5(EntityPlayerMP requester, int requestId, EaglercraftUUID uuid) {
		EntityPlayerMP target = playerList.getPlayer(toJavaUUID(uuid));
		if (target != null && target.textureData != null) {
			requester.connection.sendEaglerMessage(target.textureData.getCapeV5(requestId,
					requester.connection.getEaglerMessageProtocol()));
		} else {
			requester.connection.sendEaglerMessage(new SPacketOtherCapePresetV5EAG(requestId, 0));
		}
	}

	public void handleRequestPlayerTexturesV5(EntityPlayerMP requester, int requestId, EaglercraftUUID uuid) {
		EntityPlayerMP target = playerList.getPlayer(toJavaUUID(uuid));
		if (target != null && target.textureData != null) {
			requester.connection.sendEaglerMessage(target.textureData.getTexturesV5(requestId,
					requester.connection.getEaglerMessageProtocol()));
		} else {
			requester.connection
					.sendEaglerMessage(new SPacketOtherTexturesV5EAG(requestId, 0, null, 0, null));
		}
	}

	public void handleRequestSkinByURL(EntityPlayerMP requester, EaglercraftUUID uuid, String url) {
		url = url.toLowerCase();
		if (url.startsWith("eagler://")) {
			url = url.substring(9);
			if (!url.contains(VFile2.pathSeperator)) {
				CustomSkullData skull = skullHandler.loadSkullData(url);
				if (skull != null) {
					requester.connection.sendEaglerMessage(skull.getSkin(uuid.msb, uuid.lsb,
							requester.connection.getEaglerMessageProtocol()));
					return;
				}
			}
		}
		requester.connection.sendEaglerMessage(new SPacketOtherSkinPresetEAG(uuid.msb, uuid.lsb, 0));
	}

	public void handleRequestSkinByURLV5(EntityPlayerMP requester, int requestId, String url) {
		url = url.toLowerCase();
		if (url.startsWith("eagler://")) {
			url = url.substring(9);
			if (!url.contains(VFile2.pathSeperator)) {
				CustomSkullData skull = skullHandler.loadSkullData(url);
				if (skull != null) {
					requester.connection.sendEaglerMessage(skull.getSkinV5(requestId,
							requester.connection.getEaglerMessageProtocol()));
					return;
				}
			}
		}
		requester.connection.sendEaglerMessage(new SPacketOtherSkinPresetV5EAG(requestId, 0));
	}

	public void handleInstallNewSkin(EntityPlayerMP requester, byte[] skullData) {
		if (requester.getServer().getPermissionLevel(requester.getGameProfile()) < 2) {
			requester.sendMessage(new TextComponentTranslation("command.skull.nopermission").applyTextStyle(TextFormatting.RED));
			return;
		}
		String fileName = "eagler://" + skullHandler.installNewSkull(skullData);
		NBTTagCompound rootTagCompound = new NBTTagCompound();
		String texturesProp = "{\"textures\":{\"SKIN\":{\"url\":\"" + fileName + "\",\"metadata\":{\"model\":\"default\"}}}}";
		GameProfile profile = new GameProfile(
				toJavaUUID(EaglercraftUUID.nameUUIDFromBytes(("EaglerSkullUUID:" + fileName).getBytes(StandardCharsets.UTF_8))), "Eagler");
		profile.getProperties().put("textures",
				new Property("textures", Base64.encodeBase64String(texturesProp.getBytes(StandardCharsets.UTF_8))));
		rootTagCompound.put("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), profile));
		NBTTagCompound displayTagCompound = new NBTTagCompound();
		NBTTagList loreList = new NBTTagList();
		ITextComponent lore = new TextComponentString(fileName.length() > 24 ? (fileName.substring(0, 22) + "...") : fileName)
				.applyTextStyle(TextFormatting.GRAY);
		loreList.add(new NBTTagString(ITextComponent.Serializer.toJson(lore)));
		displayTagCompound.put("Lore", loreList);
		rootTagCompound.put("display", displayTagCompound);
		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
		stack.setTag(rootTagCompound);
		stack.setDisplayName(new TextComponentString("Custom Eaglercraft Skull").applyTextStyle(TextFormatting.RESET));
		boolean flag = requester.inventory.addItemStackToInventory(stack);
		if (flag) {
			requester.world.playSound((EntityPlayerMP)null, requester.posX, requester.posY, requester.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
					((requester.getRNG().nextFloat() - requester.getRNG().nextFloat()) * 0.7F + 1.0F)
							* 2.0F);
			requester.inventoryContainer.detectAndSendChanges();
		}
		requester.sendMessage(new TextComponentTranslation("command.skull.feedback", fileName));
	}

	public void flushCache() {
		skullHandler.flushCache();
	}

}
