package net.minecraft.util.datafix.fixes;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class StringToUUID implements IFixableData {
	public int getFixVersion() {
		return 108;
	}

	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		if (compound.hasKey("UUID", 8)) {
			compound.setUniqueId("UUID", EaglercraftUUID.fromString(compound.getString("UUID")));
		}

		return compound;
	}
}
