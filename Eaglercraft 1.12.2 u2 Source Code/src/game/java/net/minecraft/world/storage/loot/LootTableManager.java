package net.minecraft.world.storage.loot;

import com.google.common.cache.CacheProvider;
import com.google.common.cache.LoadingCache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class LootTableManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final LoadingCache<ResourceLocation, LootTable> registeredLootTables = new LoadingCache<ResourceLocation, LootTable>(
			new LootTableManager.Loader());
	private final VFile2 baseFolder;

	public LootTableManager(@Nullable VFile2 folder) {
		this.baseFolder = folder;
		this.reloadLootTables();
	}

	public LootTable getLootTableFromLocation(ResourceLocation ressources) {
		return this.registeredLootTables.get(ressources);
	}

	public void reloadLootTables() {
		this.registeredLootTables.invalidateAll();

		for (ResourceLocation resourcelocation : LootTableList.getAll()) {
			this.getLootTableFromLocation(resourcelocation);
		}
	}

	class Loader implements CacheProvider<ResourceLocation, LootTable> {
		private Loader() {
		}

		public LootTable create(ResourceLocation p_load_1_) {
			if (p_load_1_.getResourcePath().contains(".")) {
				LootTableManager.LOGGER.debug("Invalid loot table name '{}' (can't contain periods)",
						(Object) p_load_1_);
				return LootTable.EMPTY_LOOT_TABLE;
			} else {
				LootTable loottable = this.loadLootTable(p_load_1_);

				if (loottable == null) {
					loottable = this.loadBuiltinLootTable(p_load_1_);
				}

				if (loottable == null) {
					loottable = LootTable.EMPTY_LOOT_TABLE;
					LootTableManager.LOGGER.warn("Couldn't find resource table {}", (Object) p_load_1_);
				}

				return loottable;
			}
		}

		@Nullable
		private LootTable loadLootTable(ResourceLocation resource) {
			if (LootTableManager.this.baseFolder == null) {
				return null;
			} else {
				VFile2 file1 = new VFile2(new VFile2(LootTableManager.this.baseFolder, resource.getResourceDomain()),
						resource.getResourcePath() + ".json");

				if (file1.exists()) {
					String s;

					try {
						s = IOUtils.inputStreamToString(file1.getInputStream(), StandardCharsets.UTF_8);
					} catch (IOException ioexception) {
						LootTableManager.LOGGER.warn("Couldn't load loot table {} from {}", resource, file1,
								ioexception);
						return LootTable.EMPTY_LOOT_TABLE;
					}

					try {
						return (LootTable) JSONTypeProvider.deserialize(new JSONObject(s), LootTable.class);
					} catch (IllegalArgumentException | JSONException jsonparseexception) {
						LootTableManager.LOGGER.error("Couldn't load loot table {} from {}", resource, file1,
								jsonparseexception);
						return LootTable.EMPTY_LOOT_TABLE;
					}
				} else {
					return null;
				}
			}
		}

		@Nullable
		private LootTable loadBuiltinLootTable(ResourceLocation resource) {
			InputStream eis = EagRuntime.getResourceStream(
					"/assets/" + resource.getResourceDomain() + "/loot_tables/" + resource.getResourcePath() + ".json");

			if (eis != null) {
				String s;

				try {
					s = IOUtils.inputStreamToString(eis, StandardCharsets.UTF_8);
				} catch (Exception ioexception) {
					LootTableManager.LOGGER.warn("Couldn't load loot table {} from {}", resource, "/assets/"
							+ resource.getResourceDomain() + "/loot_tables/" + resource.getResourcePath() + ".json",
							ioexception);
					return LootTable.EMPTY_LOOT_TABLE;
				}

				try {
					return (LootTable) JSONTypeProvider.deserialize(new JSONObject(s), LootTable.class);
				} catch (JSONException jsonparseexception) {
					LootTableManager.LOGGER.error("Couldn't load loot table {} from {}", resource, "/assets/"
							+ resource.getResourceDomain() + "/loot_tables/" + resource.getResourcePath() + ".json",
							jsonparseexception);
					return LootTable.EMPTY_LOOT_TABLE;
				}
			} else {
				return null;
			}
		}
	}
}
