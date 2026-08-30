package net.lax1dude.eaglercraft.v1_8;

import java.util.UUID;

public class EaglerUUIDHelper {

	public static UUID fromBits(long msb, long lsb) {
		return UUID.fromString(new EaglercraftUUID(msb, lsb).toString());
	}

	public static long getMostSignificantBits(UUID uuid) {
		return EaglercraftUUID.fromString(uuid.toString()).getMostSignificantBits();
	}

	public static long getLeastSignificantBits(UUID uuid) {
		return EaglercraftUUID.fromString(uuid.toString()).getLeastSignificantBits();
	}

	public static UUID nameUUIDFromBytes(byte[] bytes) {
		return UUID.fromString(EaglercraftUUID.nameUUIDFromBytes(bytes).toString());
	}

	public static UUID randomUUID() {
		return UUID.fromString(EaglercraftUUID.randomUUID().toString());
	}

}
