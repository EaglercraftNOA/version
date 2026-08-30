package net.minecraft.src;

class EnumOptionsMappingHelper {
	static final int[] field_20155_a = new int[EnumOptions.values().length];

	static {
		try {
			field_20155_a[EnumOptions.INVERT_MOUSE.ordinal()] = 1;
		} catch (NoSuchFieldError var4) {
		}

		try {
			field_20155_a[EnumOptions.VIEW_BOBBING.ordinal()] = 2;
		} catch (NoSuchFieldError var3) {
		}

		try {
			field_20155_a[EnumOptions.ANAGLYPH.ordinal()] = 3;
		} catch (NoSuchFieldError var2) {
		}

		try {
			field_20155_a[EnumOptions.LIMIT_FRAMERATE.ordinal()] = 4;
		} catch (NoSuchFieldError var1) {
		}

	}
}
