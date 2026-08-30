package net.minecraft.network.datasync;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ReportedException;
import org.apache.commons.lang3.ObjectUtils;

public class EntityDataManager {
	/** The entity that this data manager is for. */
	private final Entity entity;
	private final IntObjectMap< EntityDataManager.DataEntry<?>> entries = new IntObjectHashMap<>();
	private boolean empty = true;
	private boolean dirty;

	public EntityDataManager(Entity entityIn) {
		this.entity = entityIn;
	}

	public static <T> DataParameter<T> createKey(int id, DataSerializer<T> serializer) {
		return new DataParameter<T>(id, serializer);
	}

	public <T> void register(DataParameter<T> key, T value) {
		int i = key.getSerializerID();

		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
		} else if (this.entries.containsKey(Integer.valueOf(i))) {
			throw new IllegalArgumentException("Duplicate id value for " + i + "!");
		} else if (DataSerializers.getSerializerId(key.getSerializerDataType()) < 0) {
			throw new IllegalArgumentException(
					"Unregistered serializer " + key.getSerializerDataType() + " for " + i + "!");
		} else {
			EntityDataManager.DataEntry<T> dataentry = new EntityDataManager.DataEntry<T>(key, value);
			this.entries.put(Integer.valueOf(i), dataentry);
			this.empty = false;
		}
	}

	private <T> EntityDataManager.DataEntry<T> getEntry(DataParameter<T> key) {
		EntityDataManager.DataEntry<T> dataentry;

		try {
			dataentry = (EntityDataManager.DataEntry) this.entries.get(Integer.valueOf(key.getSerializerID()));
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting synched entity data");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Synched entity data");
			crashreportcategory.addCrashSection("Data ID", key);
			throw new ReportedException(crashreport);
		}

		return dataentry;
	}

	/**
	 * + gets the bytevalue of a watchable object
	 */
	public <T> byte getByte(DataParameter<T> id) {
		Object flag = this.getEntry(id).getValue();
		if (flag instanceof Integer) {
			return ((Integer) flag).byteValue();
		} else {
			return ((Byte) flag).byteValue();
		}
	}

	public <T> boolean getBoolean(DataParameter<T> id) {
		Object flag = this.getEntry(id).getValue();
		if (flag instanceof Boolean) {
			return ((Boolean) flag).booleanValue();
		} else {
			return ((Byte) flag).byteValue() != 0;
		}
	}

	public <T> T get(DataParameter<T> key) {
		return (T) this.getEntry(key).getValue();
	}

	public <T> void set(DataParameter<T> key, T value) {
		EntityDataManager.DataEntry<T> dataentry = this.<T>getEntry(key);

		if (ObjectUtils.notEqual(value, dataentry.getValue())) {
			dataentry.setValue(value);
			this.entity.notifyDataManagerChange(key);
			dataentry.setDirty(true);
			this.dirty = true;
		}
	}

	public <T> void setDirty(DataParameter<T> key) {
		this.getEntry(key).dirty = true;
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public static void writeEntries(List<EntityDataManager.DataEntry<?>> entriesIn, PacketBuffer buf)
			throws IOException {
		if (entriesIn != null) {
			int i = 0;

			for (int j = entriesIn.size(); i < j; ++i) {
				EntityDataManager.DataEntry<?> dataentry = (EntityDataManager.DataEntry) entriesIn.get(i);
				writeEntry(buf, dataentry);
			}
		}

		buf.writeByte(255);
	}

	public List<EntityDataManager.DataEntry<?>> getDirty() {
		List<EntityDataManager.DataEntry<?>> list = null;

		if (this.dirty) {
			for (ObjectCursor<EntityDataManager.DataEntry<?>> dataentry1 : this.entries.values()) {
				EntityDataManager.DataEntry<?> dataentry = dataentry1.value;
				if (dataentry.isDirty()) {
					dataentry.setDirty(false);

					if (list == null) {
						list = Lists.<EntityDataManager.DataEntry<?>>newArrayList();
					}

					list.add(dataentry.func_192735_d());
				}
			}
		}

		this.dirty = false;
		return list;
	}

	public void writeEntries(PacketBuffer buf) throws IOException {
		for (ObjectCursor<EntityDataManager.DataEntry<?>> dataentry : this.entries.values()) {
			writeEntry(buf, dataentry.value);
		}

		buf.writeByte(255);
	}

	public List<EntityDataManager.DataEntry<?>> getAll() {
		List<EntityDataManager.DataEntry<?>> list = null;

		for (ObjectCursor<EntityDataManager.DataEntry<?>> dataentry : this.entries.values()) {
			if (list == null) {
				list = Lists.<EntityDataManager.DataEntry<?>>newArrayList();
			}

			list.add(dataentry.value.func_192735_d());
		}

		return list;
	}

	private static <T> void writeEntry(PacketBuffer buf, EntityDataManager.DataEntry<T> entry) throws IOException {
		DataParameter<T> dataparameter = entry.getKey();
		int i = DataSerializers.getSerializerId(dataparameter.getSerializerDataType());

		if (i < 0) {
			throw new RuntimeException("[EncoderException][EntityDataManager] Unknown serializer type "
					+ dataparameter.getSerializerDataType());
		} else {
			buf.writeByte(dataparameter.getSerializerID());
			buf.writeVarIntToBuffer(i);
			dataparameter.getSerializerDataType().write(buf, entry.getValue());
		}
	}

	public static List<EntityDataManager.DataEntry<?>> readEntries(PacketBuffer buf) throws IOException {
		List<EntityDataManager.DataEntry<?>> list = null;
		int i;

		while ((i = buf.readUnsignedByte()) != 255) {
			if (list == null) {
				list = Lists.<EntityDataManager.DataEntry<?>>newArrayList();
			}

			int j = buf.readVarIntFromBuffer();
			DataSerializer<?> dataserializer = DataSerializers.getSerializer(j);

			if (dataserializer == null) {
				throw new RuntimeException("[DecoderException][EntityDataManager] Unknown serializer type " + j);
			}

			list.add(new EntityDataManager.DataEntry(dataserializer.createKey(i), dataserializer.read(buf)));
		}

		return list;
	}

	public void setEntryValues(List<EntityDataManager.DataEntry<?>> entriesIn) {
		for (EntityDataManager.DataEntry<?> dataentry : entriesIn) {
			EntityDataManager.DataEntry<?> dataentry1 = (EntityDataManager.DataEntry) this.entries
					.get(Integer.valueOf(dataentry.getKey().getSerializerID()));

			if (dataentry1 != null) {
				this.setEntryValue(dataentry1, dataentry);
				this.entity.notifyDataManagerChange(dataentry.getKey());
			}
		}

		this.dirty = true;
	}

	protected <T> void setEntryValue(EntityDataManager.DataEntry<T> target, EntityDataManager.DataEntry<?> source) {
		target.setValue((T) source.getValue());
	}

	public boolean isEmpty() {
		return this.empty;
	}

	public void setClean() {
		this.dirty = false;

		for (ObjectCursor<EntityDataManager.DataEntry<?>> dataentry : this.entries.values()) {
			dataentry.value.setDirty(false);
		}
	}

	public static class DataEntry<T> {
		private final DataParameter<T> key;
		private T value;
		private boolean dirty;

		public DataEntry(DataParameter<T> keyIn, T valueIn) {
			this.key = keyIn;
			this.value = valueIn;
			this.dirty = true;
		}

		public DataParameter<T> getKey() {
			return this.key;
		}

		public void setValue(T valueIn) {
			this.value = valueIn;
		}

		public T getValue() {
			return this.value;
		}

		public boolean isDirty() {
			return this.dirty;
		}

		public void setDirty(boolean dirtyIn) {
			this.dirty = dirtyIn;
		}

		public EntityDataManager.DataEntry<T> func_192735_d() {
			return new EntityDataManager.DataEntry<T>(this.key, this.value);
		}
	}
}
