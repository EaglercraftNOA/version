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

package net.lax1dude.eaglercraft.sp.server.skins;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.Base64;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherCapePresetEAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherCapePresetV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherSkinPresetEAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherSkinPresetV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherTexturesV5EAG;
import net.lax1dude.eaglercraft.sp.server.EaglerPlayerList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class IntegratedTextureService {

	private final EaglerPlayerList playerList;
	private final CustomSkullLoader skullHandler;

	public IntegratedTextureService(EaglerPlayerList playerList, VFile2 file) {
		this.playerList = playerList;
		this.skullHandler = new CustomSkullLoader(file);
	}

	public void handleRequestPlayerSkin(EntityPlayerMP requester, EaglercraftUUID uuid) {

	}

	public void handleRequestPlayerCape(EntityPlayerMP requester, EaglercraftUUID uuid) {
	}

	public void handleRequestPlayerSkinV5(EntityPlayerMP requester, int requestId, EaglercraftUUID uuid) {
	}

	public void handleRequestPlayerCapeV5(EntityPlayerMP requester, int requestId, EaglercraftUUID uuid) {

	}

	public void handleRequestPlayerTexturesV5(EntityPlayerMP requester, int requestId, EaglercraftUUID uuid) {

	}

	public void handleRequestSkinByURL(EntityPlayerMP requester, EaglercraftUUID uuid, String url) {
		url = url.toLowerCase();
		if (url.startsWith("eagler://")) {
			url = url.substring(9);
			if (!url.contains(VFile2.pathSeperator)) {
				CustomSkullData skull = skullHandler.loadSkullData(url);
				if (skull != null) {

					return;
				}
			}
		}
		requester.playerNetServerHandler.sendEaglerMessage(new SPacketOtherSkinPresetEAG(uuid.msb, uuid.lsb, 0));
	}

	public void handleRequestSkinByURLV5(EntityPlayerMP requester, int requestId, String url) {
		url = url.toLowerCase();
		if (url.startsWith("eagler://")) {
			url = url.substring(9);
			if (!url.contains(VFile2.pathSeperator)) {
				CustomSkullData skull = skullHandler.loadSkullData(url);
				if (skull != null) {

					return;
				}
			}
		}
		requester.playerNetServerHandler.sendEaglerMessage(new SPacketOtherSkinPresetV5EAG(requestId, 0));
	}

	public void handleInstallNewSkin(EntityPlayerMP requester, byte[] skullData) {
		if (!requester.canCommandSenderUseCommand(2, "give")) {
			ChatComponentTranslation cc = new ChatComponentTranslation("command.skull.nopermission");
			cc.getChatStyle().setColor(EnumChatFormatting.RED);
			requester.addChatMessage(cc);
			return;
		}
		String fileName = "eagler://" + skullHandler.installNewSkull(skullData);
		NBTTagCompound rootTagCompound = new NBTTagCompound();
		NBTTagCompound ownerTagCompound = new NBTTagCompound();
		ownerTagCompound.setString("Name", "Eagler");
		ownerTagCompound.setString("Id", EaglercraftUUID
				.nameUUIDFromBytes((("EaglerSkullUUID:" + fileName).getBytes(StandardCharsets.UTF_8))).toString());
		NBTTagCompound propertiesTagCompound = new NBTTagCompound();
		NBTTagList texturesTagList = new NBTTagList();
		NBTTagCompound texturesTagCompound = new NBTTagCompound();
		String texturesProp = "{\"textures\":{\"SKIN\":{\"url\":\"" + fileName
				+ "\",\"metadata\":{\"model\":\"default\"}}}}";
		texturesTagCompound.setString("Value",
				Base64.encodeBase64String(texturesProp.getBytes(StandardCharsets.UTF_8)));
		texturesTagList.appendTag(texturesTagCompound);
		propertiesTagCompound.setTag("textures", texturesTagList);
		ownerTagCompound.setTag("Properties", propertiesTagCompound);
		rootTagCompound.setTag("SkullOwner", ownerTagCompound);
		NBTTagCompound displayTagCompound = new NBTTagCompound();
		displayTagCompound.setString("Name", EnumChatFormatting.RESET + "Custom Eaglercraft Skull");
		NBTTagList loreList = new NBTTagList();
		loreList.appendTag(new NBTTagString(
				EnumChatFormatting.GRAY + (fileName.length() > 24 ? (fileName.substring(0, 22) + "...") : fileName)));
		displayTagCompound.setTag("Lore", loreList);
		rootTagCompound.setTag("display", displayTagCompound);
		ItemStack stack = new ItemStack(Items.skull, 1, 3);
		stack.setTagCompound(rootTagCompound);
		boolean flag = requester.inventory.addItemStackToInventory(stack);
		if (flag) {
			requester.worldObj.playSoundAtEntity(requester, "random.pop", 0.2F,
					((requester.getRNG().nextFloat() - requester.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
			requester.inventoryContainer.detectAndSendChanges();
		}
		requester.addChatMessage(new ChatComponentTranslation("command.skull.feedback", fileName));
	}

	public void flushCache() {
		skullHandler.flushCache();
	}

}
