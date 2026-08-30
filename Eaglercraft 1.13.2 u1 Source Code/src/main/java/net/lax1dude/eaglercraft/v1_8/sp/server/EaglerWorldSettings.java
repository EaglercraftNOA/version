package net.lax1dude.eaglercraft.v1_8.sp.server;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import com.google.gson.JsonParser;

public class EaglerWorldSettings {

	public final String worldName;
	public final GameType gameType;
	public final int worldTypeId;
	public final String worldArgs;
	public final long seed;
	public final boolean allowCommands;
	public final boolean generateStructures;
	public final boolean bonusChest;
	public final boolean hardcore;
	public final List<String> dataPacks;

	public EaglerWorldSettings(String worldName, int gameType, int worldTypeId, String worldArgs, long seed,
			boolean allowCommands, boolean generateStructures, boolean bonusChest, boolean hardcore,
			List<String> dataPacks) {
		this.worldName = worldName;
		this.gameType = GameType.getByID(gameType);
		this.worldTypeId = worldTypeId;
		this.worldArgs = worldArgs != null ? worldArgs : "";
		this.seed = seed;
		this.allowCommands = allowCommands;
		this.generateStructures = generateStructures;
		this.bonusChest = bonusChest;
		this.hardcore = hardcore;
		this.dataPacks = dataPacks != null ? dataPacks : Collections.emptyList();
	}

	public WorldSettings createWorldSettings() {
		WorldSettings settings = new WorldSettings(seed, gameType, generateStructures, hardcore, getWorldType(worldTypeId));
		settings.setGeneratorOptions((new JsonParser()).parse(worldArgs == null || worldArgs.length() == 0 ? "{}" : worldArgs));
		if(allowCommands) {
			settings.enableCommands();
		}
		if(bonusChest) {
			settings.enableBonusChest();
		}
		return settings;
	}

	private static WorldType getWorldType(int worldTypeId) {
		switch (worldTypeId) {
		case 1:
			return WorldType.FLAT;
		case 2:
			return WorldType.LARGE_BIOMES;
		case 3:
			return WorldType.AMPLIFIED;
		case 5:
			return WorldType.DEBUG_ALL_BLOCK_STATES;
		default:
			return WorldType.DEFAULT;
		}
	}
}
