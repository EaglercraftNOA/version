package net.lax1dude.eaglercraft.sp.ipc;

import java.util.Map;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;

public class IPCPacketMapAssets implements IPCPacketBase {

	public static final int ID = 0x39;
	public Map<String, byte[]> assets = new HashMap<String, byte[]>();

	public IPCPacketMapAssets() {
	}

	public IPCPacketMapAssets(Map<String, byte[]> assets) {
		this.assets = assets;
	}

	@Override
	public void deserialize(DataInput bin) throws IOException {
		assets.clear();
		int i = bin.readInt();
		for (int j = 0; j < i; j++) {
			String s = bin.readUTF();
			int dataSize = bin.readInt();
			byte[] data = new byte[dataSize];
			bin.readFully(data);
			assets.put(s, data);
		}
	}

	@Override
	public void serialize(DataOutput bin) throws IOException {
		int i = assets.size();
		bin.writeInt(i);
		for (Map.Entry<String, byte[]> entry : assets.entrySet()) {
			bin.writeUTF(entry.getKey());
			bin.writeInt(entry.getValue().length);
			bin.write(entry.getValue());
		}
	}

	@Override
	public int id() {
		return ID;
	}

	@Override
	public int size() {
		int len = 4;
		for (Map.Entry<String, byte[]> entry : assets.entrySet()) {
			len += IPCPacketBase.strLen(entry.getKey());
			len += 4;
			len += entry.getValue().length;
		}

		return len;
	}
}
