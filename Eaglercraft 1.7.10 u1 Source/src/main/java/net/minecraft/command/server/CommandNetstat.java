package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkStatistics;
import net.minecraft.util.ChatComponentText;

public class CommandNetstat extends CommandBase {
	private static final String __OBFID = "CL_00001904";

	public String getCommandName() {
		return "netstat";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 0;
	}

	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "commands.players.usage";
	}

	public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {

	}

	private void func_152375_a(ICommandSender p_152375_1_, int p_152375_2_, NetworkStatistics.PacketStat p_152375_3_) {
		if (p_152375_3_ != null) {
			p_152375_1_.addChatMessage(new ChatComponentText(p_152375_3_.toString()));
		} else {
			p_152375_1_.addChatMessage(new ChatComponentText("Packet " + p_152375_2_ + " not found!"));
		}
	}
}
