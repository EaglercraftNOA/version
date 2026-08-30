package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

public class StatisticsManagerServer extends StatisticsManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftServer mcServer;
	private final VFile2 statsFile;
	private final Set<StatBase> dirty = Sets.<StatBase>newHashSet();
	private int lastStatRequest = -300;

	public StatisticsManagerServer(MinecraftServer serverIn, VFile2 statsFileIn) {
		this.mcServer = serverIn;
		this.statsFile = statsFileIn;
	}

	public void readStatFile() {
		if (this.statsFile.exists()) {
			try {
				this.statsData.clear();
				this.statsData.putAll(this.parseJson(
						IOUtils.inputStreamToString(this.statsFile.getInputStream(), StandardCharsets.UTF_8)));
			} catch (IOException ioexception) {
				LOGGER.error("Couldn't read statistics file {}", this.statsFile, ioexception);
			} catch (JSONException jsonparseexception) {
				LOGGER.error("Couldn't parse statistics file {}", this.statsFile, jsonparseexception);
			}
		}
	}

	public void saveStatFile() {
		this.statsFile.setAllChars(dumpJson(this.statsData));
	}

	/**
	 * Triggers the logging of an achievement and attempts to announce to server
	 */
	public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int p_150873_3_) {
		super.unlockAchievement(playerIn, statIn, p_150873_3_);
		this.dirty.add(statIn);
	}

	private Set<StatBase> getDirty() {
		Set<StatBase> set = Sets.newHashSet(this.dirty);
		this.dirty.clear();
		return set;
	}

	public Map<StatBase, TupleIntJsonSerializable> parseJson(String p_150881_1_) {
		Object jsonelement = JSONTypeProvider.parse(p_150881_1_);

		if (!(jsonelement instanceof JSONObject)) {
			return Maps.<StatBase, TupleIntJsonSerializable>newHashMap();
		} else {
			JSONObject jsonobject = (JSONObject) jsonelement;
			Map<StatBase, TupleIntJsonSerializable> map = Maps.<StatBase, TupleIntJsonSerializable>newHashMap();

			for (String entry : jsonobject.keySet()) {
				StatBase statbase = StatList.getOneShotStat(entry);

				if (statbase != null) {
					TupleIntJsonSerializable tupleintjsonserializable = new TupleIntJsonSerializable();

					if (jsonobject.get(entry) instanceof Number) {
						tupleintjsonserializable.setIntegerValue(((Number) jsonobject.get(entry)).intValue());
					} else if (jsonobject.get(entry) instanceof JSONObject) {
						JSONObject jsonobject1 = (JSONObject) jsonobject.get(entry);

						if (jsonobject1.has("value") && jsonobject1.get("value") instanceof Number) {
							tupleintjsonserializable.setIntegerValue(((Number) jsonobject1.get("value")).intValue());
						}

						//TODO: serializableClass is always null, never set
//						if (jsonobject1.has("progress") && statbase.getSerializableClazz() != null) {
//							try {
//								Constructor<? extends IJsonSerializable> constructor = statbase.getSerializableClazz()
//										.getConstructor();
//								IJsonSerializable ijsonserializable = constructor.newInstance();
//								ijsonserializable.fromJson(jsonobject1.get("progress"));
//								tupleintjsonserializable.setJsonSerializableValue(ijsonserializable);
//							} catch (Throwable throwable) {
//								LOGGER.warn("Invalid statistic progress in {}", this.statsFile, throwable);
//							}
//						}
					}

					map.put(statbase, tupleintjsonserializable);
				} else {
					LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.statsFile, entry);
				}
			}

			return map;
		}
	}

	public static String dumpJson(Map<StatBase, TupleIntJsonSerializable> p_150880_0_) {
		JSONObject jsonobject = new JSONObject();

		for (Entry<StatBase, TupleIntJsonSerializable> entry : p_150880_0_.entrySet()) {
			if (((TupleIntJsonSerializable) entry.getValue()).getJsonSerializableValue() != null) {
				JSONObject jsonobject1 = new JSONObject();
				jsonobject1.put("value",
						Integer.valueOf(((TupleIntJsonSerializable) entry.getValue()).getIntegerValue()));

				try {
					jsonobject1.put("progress", ((TupleIntJsonSerializable) entry.getValue()).getJsonSerializableValue()
							.getSerializableElement());
				} catch (Throwable throwable) {
					LOGGER.warn("Couldn't save statistic {}: error serializing progress",
							((StatBase) entry.getKey()).getStatName(), throwable);
				}

				jsonobject.put((entry.getKey()).statId, jsonobject1);
			} else {
				jsonobject.put((entry.getKey()).statId,
						Integer.valueOf(((TupleIntJsonSerializable) entry.getValue()).getIntegerValue()));
			}
		}

		return jsonobject.toString();
	}

	public void markAllDirty() {
		this.dirty.addAll(this.statsData.keySet());
	}

	public void sendStats(EntityPlayerMP player) {
		int i = this.mcServer.getTickCounter();
		Map<StatBase, Integer> map = Maps.<StatBase, Integer>newHashMap();

		if (i - this.lastStatRequest > 300) {
			this.lastStatRequest = i;

			for (StatBase statbase : this.getDirty()) {
				map.put(statbase, Integer.valueOf(this.readStat(statbase)));
			}
		}

		player.connection.sendPacket(new SPacketStatistics(map));
	}
}
