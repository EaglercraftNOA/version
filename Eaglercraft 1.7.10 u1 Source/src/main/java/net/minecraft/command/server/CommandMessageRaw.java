package net.minecraft.command.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.command.*;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.json.JSONException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandMessageRaw extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "tellraw";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 2;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.tellraw.usage";
	}

	@Override
	public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {

	}

	/**
	 * Callback for when the command is executed
	 */
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new WrongUsageException("commands.tellraw.usage", new Object[0]);
		} else {
			EntityPlayer entityplayer = getPlayer(sender, args[0]);
			String s = func_82360_a(entityplayer, args, 1);

			try {
				IChatComponent itextcomponent = IChatComponent.Serializer.fromJson(s);
				entityplayer.addChatMessage(itextcomponent);
			} catch (JSONException jsonparseexception) {
				throw new SyntaxErrorException(jsonparseexception.toString());
			}
		}
	}

	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		return p_71516_2_.length == 1
				? getListOfStringsMatchingLastWord(p_71516_2_, MinecraftServer.getServer().getAllUsernames())
				: null;
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}
}
