package net.minecraft.server.management;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter {
	private static final Logger field_152732_e = LogManager.getLogger();
	public static final File field_152728_a = new File("banned-ips.txt");
	public static final File field_152729_b = new File("banned-players.txt");
	public static final File field_152730_c = new File("ops.txt");
	public static final File field_152731_d = new File("white-list.txt");
	private static final String __OBFID = "CL_00001882";

	private static void func_152717_a(MinecraftServer p_152717_0_, Collection p_152717_1_) {

	}

	public static String func_152719_a(String p_152719_0_) {
		return "hi";
	}
}
