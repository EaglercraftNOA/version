package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.gen.structure.MapGenStructureData;

public class MapStorage {
	private final ISaveHandler saveHandler;
	protected Map<String, WorldSavedData> loadedDataMap = Maps.<String, WorldSavedData>newHashMap();
	private final List<WorldSavedData> loadedDataList = Lists.<WorldSavedData>newArrayList();
	private final Map<String, Short> idCounts = Maps.<String, Short>newHashMap();

	public static interface MapStorageProvider {
		WorldSavedData createInstance(String mapFileName);
	}

	public static final Map<Class<? extends WorldSavedData>, MapStorageProvider> storageProviders = new HashMap();

	static {
		storageProviders.put(MapData.class, MapData::new);
		storageProviders.put(MapGenStructureData.class, MapGenStructureData::new);
		storageProviders.put(VillageCollection.class, VillageCollection::new);
		storageProviders.put(ScoreboardSaveData.class, ScoreboardSaveData::new);
	}

	public MapStorage(ISaveHandler saveHandlerIn) {
		this.saveHandler = saveHandlerIn;
		this.loadIdCounts();
	}

	@Nullable

	/**
	 * Loads an existing MapDataBase corresponding to the given id from disk,
	 * instantiating the given Class, or returns null if none such file exists.
	 */
	public WorldSavedData getOrLoadData(Class<? extends WorldSavedData> clazz, String dataIdentifier) {
		WorldSavedData worldsaveddata = this.loadedDataMap.get(dataIdentifier);

		if (worldsaveddata != null) {
			return worldsaveddata;
		} else {
			if (this.saveHandler != null) {
				try {
					VFile2 file1 = this.saveHandler.getMapFileFromName(dataIdentifier);

					if (file1 != null && file1.exists()) {
						try {
							worldsaveddata = (WorldSavedData) storageProviders.get(clazz)
									.createInstance(dataIdentifier);
						} catch (Exception exception) {
							throw new RuntimeException("Failed to instantiate " + clazz, exception);
						}

						InputStream fileinputstream = file1.getInputStream();
						NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
						fileinputstream.close();
						worldsaveddata.readFromNBT(nbttagcompound.getCompoundTag("data"));
					}
				} catch (Exception exception1) {
					exception1.printStackTrace();
				}
			}

			if (worldsaveddata != null) {
				this.loadedDataMap.put(dataIdentifier, worldsaveddata);
				this.loadedDataList.add(worldsaveddata);
			}

			return worldsaveddata;
		}
	}

	/**
	 * Assigns the given String id to the given MapDataBase, removing any existing
	 * ones of the same id.
	 */
	public void setData(String dataIdentifier, WorldSavedData data) {
		if (this.loadedDataMap.containsKey(dataIdentifier)) {
			this.loadedDataList.remove(this.loadedDataMap.remove(dataIdentifier));
		}

		this.loadedDataMap.put(dataIdentifier, data);
		this.loadedDataList.add(data);
	}

	/**
	 * Saves all dirty loaded MapDataBases to disk.
	 */
	public void saveAllData() {
		for (int i = 0; i < this.loadedDataList.size(); ++i) {
			WorldSavedData worldsaveddata = this.loadedDataList.get(i);

			if (worldsaveddata.isDirty()) {
				this.saveData(worldsaveddata);
				worldsaveddata.setDirty(false);
			}
		}
	}

	/**
	 * Saves the given MapDataBase to disk.
	 */
	private void saveData(WorldSavedData data) {
		if (this.saveHandler != null) {
			try {
				VFile2 file1 = this.saveHandler.getMapFileFromName(data.mapName);

				if (file1 != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setTag("data", data.writeToNBT(new NBTTagCompound()));
					OutputStream fileoutputstream = file1.getOutputStream();
					CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
					fileoutputstream.close();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * Loads the idCounts Map from the 'idcounts' file.
	 */
	private void loadIdCounts() {
		try {
			this.idCounts.clear();

			if (this.saveHandler == null) {
				return;
			}

			VFile2 file1 = this.saveHandler.getMapFileFromName("idcounts");

			if (file1 != null && file1.exists()) {
				DataInputStream datainputstream = new DataInputStream(file1.getInputStream());
				NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
				datainputstream.close();

				for (String s : nbttagcompound.getKeySet()) {
					NBTBase nbtbase = nbttagcompound.getTag(s);

					if (nbtbase instanceof NBTTagShort) {
						NBTTagShort nbttagshort = (NBTTagShort) nbtbase;
						short short1 = nbttagshort.getShort();
						this.idCounts.put(s, Short.valueOf(short1));
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Returns an unique new data id for the given prefix and saves the idCounts map
	 * to the 'idcounts' file.
	 */
	public int getUniqueDataId(String key) {
		Short oshort = this.idCounts.get(key);

		if (oshort == null) {
			oshort = 0;
		} else {
			oshort = (short) (oshort.shortValue() + 1);
		}

		this.idCounts.put(key, oshort);

		if (this.saveHandler == null) {
			return oshort.shortValue();
		} else {
			try {
				VFile2 file1 = this.saveHandler.getMapFileFromName("idcounts");

				if (file1 != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();

					for (String s : this.idCounts.keySet()) {
						nbttagcompound.setShort(s, ((Short) this.idCounts.get(s)).shortValue());
					}

					DataOutputStream dataoutputstream = new DataOutputStream(file1.getOutputStream());
					CompressedStreamTools.write(nbttagcompound, dataoutputstream);
					dataoutputstream.close();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			return oshort.shortValue();
		}
	}
}
