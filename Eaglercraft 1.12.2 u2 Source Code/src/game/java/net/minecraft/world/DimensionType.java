package net.minecraft.world;

import net.peyton.eagler.minecraft.WorldConstructor;

public enum DimensionType {
	OVERWORLD(0, "overworld", "", WorldProviderSurface::new),
	NETHER(-1, "the_nether", "_nether", WorldProviderHell::new), THE_END(1, "the_end", "_end", WorldProviderEnd::new);

	private final int id;
	private final String name;
	private final String suffix;
	private final WorldConstructor<? extends WorldProvider> clazz;

	private DimensionType(int idIn, String nameIn, String suffixIn, WorldConstructor<? extends WorldProvider> clazzIn) {
		this.id = idIn;
		this.name = nameIn;
		this.suffix = suffixIn;
		this.clazz = clazzIn;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getSuffix() {
		return this.suffix;
	}

	public WorldProvider createDimension() {
		try {
			return clazz.createDimension();
		} catch (Throwable illegalaccessexception) {
			throw new Error("Could not create new dimension", illegalaccessexception);
		}
	}

	public static DimensionType getById(int id) {
		for (DimensionType dimensiontype : values()) {
			if (dimensiontype.getId() == id) {
				return dimensiontype;
			}
		}

		throw new IllegalArgumentException("Invalid dimension id " + id);
	}

	public static DimensionType func_193417_a(String p_193417_0_) {
		for (DimensionType dimensiontype : values()) {
			if (dimensiontype.getName().equals(p_193417_0_)) {
				return dimensiontype;
			}
		}

		throw new IllegalArgumentException("Invalid dimension " + p_193417_0_);
	}
}
