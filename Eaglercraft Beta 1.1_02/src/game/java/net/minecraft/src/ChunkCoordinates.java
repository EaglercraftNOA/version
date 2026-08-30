package net.minecraft.src;

final class ChunkCoordinates {
	public final int field_1518_a;
	public final int field_1517_b;

	public ChunkCoordinates(int var1, int var2) {
		this.field_1518_a = var1;
		this.field_1517_b = var2;
	}

	public boolean equals(Object var1) {
		if(!(var1 instanceof ChunkCoordinates)) {
			return false;
		} else {
			ChunkCoordinates var2 = (ChunkCoordinates)var1;
			return this.field_1518_a == var2.field_1518_a && this.field_1517_b == var2.field_1517_b;
		}
	}

	public int hashCode() {
		return this.field_1518_a << 16 ^ this.field_1517_b;
	}
}
