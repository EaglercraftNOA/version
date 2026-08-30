package net.peyton.eagler.fs;

import java.util.function.Supplier;

import net.lax1dude.eaglercraft.internal.IEaglerFilesystem;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.sp.server.internal.ServerPlatformSingleplayer;

public class WorldsDB {

	private static final Supplier<IEaglerFilesystem> fsGetter = ServerPlatformSingleplayer::getWorldsDatabase;

	public static VFile2 newVFile(Object... path) {
		return VFile2.create(fsGetter, path);
	}

}
