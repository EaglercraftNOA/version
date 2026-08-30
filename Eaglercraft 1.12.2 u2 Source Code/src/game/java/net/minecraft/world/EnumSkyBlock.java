package net.minecraft.world;

public enum EnumSkyBlock {
	SKY(15), BLOCK(0);

	public final int defaultLightValue;
	
	public static final EnumSkyBlock[] _VALUES = values();

	private EnumSkyBlock(int defaultLightValueIn) {
		this.defaultLightValue = defaultLightValueIn;
	}
}
