package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;

public enum SoundCategory {
	MASTER("master", 0), MUSIC("music", 1), RECORDS("record", 2), WEATHER("weather", 3), BLOCKS("block", 4),
	HOSTILE("hostile", 5), NEUTRAL("neutral", 6), PLAYERS("player", 7), AMBIENT("ambient", 8), VOICE("voice", 9);

	private static final Map<String, SoundCategory> SOUND_CATEGORIES = Maps.<String, SoundCategory>newHashMap();
	private final String name;
	private final int categoryId;

	private SoundCategory(String nameIn, int id) {
		this.name = nameIn;
		this.categoryId = id;
	}

	public String getName() {
		return this.name;
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public static SoundCategory getByName(String categoryName) {
		return SOUND_CATEGORIES.get(categoryName);
	}

	public static Set<String> getSoundCategoryNames() {
		return SOUND_CATEGORIES.keySet();
	}

	static {
		for (SoundCategory soundcategory : values()) {
			if (SOUND_CATEGORIES.containsKey(soundcategory.getName())) {
				throw new Error("Clash in Sound Category name pools! Cannot insert " + soundcategory);
			}

			SOUND_CATEGORIES.put(soundcategory.getName(), soundcategory);
		}
	}
}
