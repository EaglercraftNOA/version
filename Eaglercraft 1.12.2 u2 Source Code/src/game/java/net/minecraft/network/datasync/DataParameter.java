package net.minecraft.network.datasync;

public class DataParameter<T> {
	private int id;
	private final DataSerializer<T> serializer;

	public DataParameter(int idIn, DataSerializer<T> serializerIn) {
		this.id = idIn;
		this.serializer = serializerIn;
	}

	public int getSerializerID() {
		return this.id;
	}

	public DataSerializer<T> getSerializerDataType() {
		return this.serializer;
	}

	public void setSerializerID(int id) {
		this.id = id;
	}
}
