package net.minecraft.world.biome;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeForestMutated extends BiomeForest {
	public BiomeForestMutated(Biome.BiomeProperties properties) {
		super(BiomeForest.Type.BIRCH, properties);
	}

	public WorldGenAbstractTree genBigTreeChance(EaglercraftRandom rand) {
		return rand.nextBoolean() ? BiomeForest.SUPER_BIRCH_TREE : BiomeForest.BIRCH_TREE;
	}
}
