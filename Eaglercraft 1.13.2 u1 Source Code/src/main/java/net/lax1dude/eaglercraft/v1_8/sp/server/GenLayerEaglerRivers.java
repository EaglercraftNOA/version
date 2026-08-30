/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 */

package net.lax1dude.eaglercraft.v1_8.sp.server;

import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public class GenLayerEaglerRivers implements IAreaTransformer1, IDimOffset0Transformer {

	private static final int[] pattern = new int[] {
			0b111000011100001110000111,
			0b111000111110011111000111,
			0b011100011100001110001110,
			0b011100000000000000001110,
			0b001110000000000000011100,
			0b001110000000000000011100,
			0b000111000000000000111000,
			0b000111000000000000111000,
			0b000011100000000001110000,
			0b000011100000000001110000,
			0b000001110000000011100000,
			0b000001110000000011100000,
			0b000000111000000111000000,
			0b000000111000000111000000,
			0b000000011100001110000000,
			0b000000011100001110000000,
			0b000000001110011100000000,
			0b000000001110011100000000,
			0b000000000111111000000000,
			0b000000000111111000000000,
			0b000000000011110000000000,
			0b000000000011110000000000,
			0b000000000001100000000000,
			0b000000000001100000000000,
	};

	private static final int PATTERN_SIZE = 24;
	private static final int RIVER_BIOME_ID = 7;
	private final long seed;

	public GenLayerEaglerRivers(long seed) {
		this.seed = seed;
	}

	@Override
	public int apply(IContextExtended<?> context, AreaDimension areaDimensionIn, IArea area, int x, int y) {
		int original = area.getValue(x, y);
		long a = seed * 6364136223846793005L + 1442695040888963407L;
		long b = ((a & 112104L) == 0L) ? (((a & 534L) == 0L) ? 1L : 15L) : 746L;
		long xx = (long)x & 0xFFFFFFFFL;
		long yy = (long)y & 0xFFFFFFFFL;
		long hash = a + (xx / PATTERN_SIZE);
		hash *= hash * 6364136223846793005L + 1442695040888963407L;
		hash += (yy / PATTERN_SIZE);
		hash *= hash * 6364136223846793005L + 1442695040888963407L;
		hash += a;
		if ((hash & b) == 0L && isRiverPixel(hash, xx, yy)) {
			return RIVER_BIOME_ID;
		}
		return original;
	}

	private static boolean isRiverPixel(long hash, long xx, long yy) {
		xx %= PATTERN_SIZE;
		yy %= PATTERN_SIZE;
		long tmp;
		switch((int)((hash >>> 16L) & 3L)) {
		case 1:
			tmp = xx;
			xx = yy;
			yy = (long)PATTERN_SIZE - tmp - 1L;
			break;
		case 2:
			tmp = xx;
			xx = (long)PATTERN_SIZE - yy - 1L;
			yy = tmp;
			break;
		case 3:
			tmp = xx;
			xx = (long)PATTERN_SIZE - yy - 1L;
			yy = (long)PATTERN_SIZE - tmp - 1L;
			break;
		default:
			break;
		}
		return (pattern[(int)yy] & (1 << (int)xx)) != 0;
	}
}
