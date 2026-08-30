package net.minecraft.src;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class MCHashTable {
	private Int2ObjectOpenHashMap<Object> slots = new Int2ObjectOpenHashMap<>(16, 0.75F);

	public Object lookup(int var1) {
		return slots.get(var1);
	}

	public void addKey(int var1, Object var2) {
		this.slots.put(var1, var2);
	}

	public Object removeObject(int var1) {
		return this.slots.remove(var1);
	}

	public void clearMap() {
		this.slots.clear();
	}
}