package net.minecraft.world.gen;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class ChunkGeneratorSettings {
	public final float coordinateScale;
	public final float heightScale;
	public final float upperLimitScale;
	public final float lowerLimitScale;
	public final float depthNoiseScaleX;
	public final float depthNoiseScaleZ;
	public final float depthNoiseScaleExponent;
	public final float mainNoiseScaleX;
	public final float mainNoiseScaleY;
	public final float mainNoiseScaleZ;
	public final float baseSize;
	public final float stretchY;
	public final float biomeDepthWeight;
	public final float biomeDepthOffSet;
	public final float biomeScaleWeight;
	public final float biomeScaleOffset;
	public final int seaLevel;
	public final boolean useCaves;
	public final boolean useDungeons;
	public final int dungeonChance;
	public final boolean useStrongholds;
	public final boolean useVillages;
	public final boolean useMineShafts;
	public final boolean useTemples;
	public final boolean useMonuments;
	public final boolean field_191077_z;
	public final boolean useRavines;
	public final boolean useWaterLakes;
	public final int waterLakeChance;
	public final boolean useLavaLakes;
	public final int lavaLakeChance;
	public final boolean useLavaOceans;
	public final int fixedBiome;
	public final int biomeSize;
	public final int riverSize;
	public final int dirtSize;
	public final int dirtCount;
	public final int dirtMinHeight;
	public final int dirtMaxHeight;
	public final int gravelSize;
	public final int gravelCount;
	public final int gravelMinHeight;
	public final int gravelMaxHeight;
	public final int graniteSize;
	public final int graniteCount;
	public final int graniteMinHeight;
	public final int graniteMaxHeight;
	public final int dioriteSize;
	public final int dioriteCount;
	public final int dioriteMinHeight;
	public final int dioriteMaxHeight;
	public final int andesiteSize;
	public final int andesiteCount;
	public final int andesiteMinHeight;
	public final int andesiteMaxHeight;
	public final int coalSize;
	public final int coalCount;
	public final int coalMinHeight;
	public final int coalMaxHeight;
	public final int ironSize;
	public final int ironCount;
	public final int ironMinHeight;
	public final int ironMaxHeight;
	public final int goldSize;
	public final int goldCount;
	public final int goldMinHeight;
	public final int goldMaxHeight;
	public final int redstoneSize;
	public final int redstoneCount;
	public final int redstoneMinHeight;
	public final int redstoneMaxHeight;
	public final int diamondSize;
	public final int diamondCount;
	public final int diamondMinHeight;
	public final int diamondMaxHeight;
	public final int lapisSize;
	public final int lapisCount;
	public final int lapisCenterHeight;
	public final int lapisSpread;

	private ChunkGeneratorSettings(ChunkGeneratorSettings.Factory settingsFactory) {
		this.coordinateScale = settingsFactory.coordinateScale;
		this.heightScale = settingsFactory.heightScale;
		this.upperLimitScale = settingsFactory.upperLimitScale;
		this.lowerLimitScale = settingsFactory.lowerLimitScale;
		this.depthNoiseScaleX = settingsFactory.depthNoiseScaleX;
		this.depthNoiseScaleZ = settingsFactory.depthNoiseScaleZ;
		this.depthNoiseScaleExponent = settingsFactory.depthNoiseScaleExponent;
		this.mainNoiseScaleX = settingsFactory.mainNoiseScaleX;
		this.mainNoiseScaleY = settingsFactory.mainNoiseScaleY;
		this.mainNoiseScaleZ = settingsFactory.mainNoiseScaleZ;
		this.baseSize = settingsFactory.baseSize;
		this.stretchY = settingsFactory.stretchY;
		this.biomeDepthWeight = settingsFactory.biomeDepthWeight;
		this.biomeDepthOffSet = settingsFactory.biomeDepthOffset;
		this.biomeScaleWeight = settingsFactory.biomeScaleWeight;
		this.biomeScaleOffset = settingsFactory.biomeScaleOffset;
		this.seaLevel = settingsFactory.seaLevel;
		this.useCaves = settingsFactory.useCaves;
		this.useDungeons = settingsFactory.useDungeons;
		this.dungeonChance = settingsFactory.dungeonChance;
		this.useStrongholds = settingsFactory.useStrongholds;
		this.useVillages = settingsFactory.useVillages;
		this.useMineShafts = settingsFactory.useMineShafts;
		this.useTemples = settingsFactory.useTemples;
		this.useMonuments = settingsFactory.useMonuments;
		this.field_191077_z = settingsFactory.field_191076_A;
		this.useRavines = settingsFactory.useRavines;
		this.useWaterLakes = settingsFactory.useWaterLakes;
		this.waterLakeChance = settingsFactory.waterLakeChance;
		this.useLavaLakes = settingsFactory.useLavaLakes;
		this.lavaLakeChance = settingsFactory.lavaLakeChance;
		this.useLavaOceans = settingsFactory.useLavaOceans;
		this.fixedBiome = settingsFactory.fixedBiome;
		this.biomeSize = settingsFactory.biomeSize;
		this.riverSize = settingsFactory.riverSize;
		this.dirtSize = settingsFactory.dirtSize;
		this.dirtCount = settingsFactory.dirtCount;
		this.dirtMinHeight = settingsFactory.dirtMinHeight;
		this.dirtMaxHeight = settingsFactory.dirtMaxHeight;
		this.gravelSize = settingsFactory.gravelSize;
		this.gravelCount = settingsFactory.gravelCount;
		this.gravelMinHeight = settingsFactory.gravelMinHeight;
		this.gravelMaxHeight = settingsFactory.gravelMaxHeight;
		this.graniteSize = settingsFactory.graniteSize;
		this.graniteCount = settingsFactory.graniteCount;
		this.graniteMinHeight = settingsFactory.graniteMinHeight;
		this.graniteMaxHeight = settingsFactory.graniteMaxHeight;
		this.dioriteSize = settingsFactory.dioriteSize;
		this.dioriteCount = settingsFactory.dioriteCount;
		this.dioriteMinHeight = settingsFactory.dioriteMinHeight;
		this.dioriteMaxHeight = settingsFactory.dioriteMaxHeight;
		this.andesiteSize = settingsFactory.andesiteSize;
		this.andesiteCount = settingsFactory.andesiteCount;
		this.andesiteMinHeight = settingsFactory.andesiteMinHeight;
		this.andesiteMaxHeight = settingsFactory.andesiteMaxHeight;
		this.coalSize = settingsFactory.coalSize;
		this.coalCount = settingsFactory.coalCount;
		this.coalMinHeight = settingsFactory.coalMinHeight;
		this.coalMaxHeight = settingsFactory.coalMaxHeight;
		this.ironSize = settingsFactory.ironSize;
		this.ironCount = settingsFactory.ironCount;
		this.ironMinHeight = settingsFactory.ironMinHeight;
		this.ironMaxHeight = settingsFactory.ironMaxHeight;
		this.goldSize = settingsFactory.goldSize;
		this.goldCount = settingsFactory.goldCount;
		this.goldMinHeight = settingsFactory.goldMinHeight;
		this.goldMaxHeight = settingsFactory.goldMaxHeight;
		this.redstoneSize = settingsFactory.redstoneSize;
		this.redstoneCount = settingsFactory.redstoneCount;
		this.redstoneMinHeight = settingsFactory.redstoneMinHeight;
		this.redstoneMaxHeight = settingsFactory.redstoneMaxHeight;
		this.diamondSize = settingsFactory.diamondSize;
		this.diamondCount = settingsFactory.diamondCount;
		this.diamondMinHeight = settingsFactory.diamondMinHeight;
		this.diamondMaxHeight = settingsFactory.diamondMaxHeight;
		this.lapisSize = settingsFactory.lapisSize;
		this.lapisCount = settingsFactory.lapisCount;
		this.lapisCenterHeight = settingsFactory.lapisCenterHeight;
		this.lapisSpread = settingsFactory.lapisSpread;
	}

	public static class Factory {
		public float coordinateScale = 684.412F;
		public float heightScale = 684.412F;
		public float upperLimitScale = 512.0F;
		public float lowerLimitScale = 512.0F;
		public float depthNoiseScaleX = 200.0F;
		public float depthNoiseScaleZ = 200.0F;
		public float depthNoiseScaleExponent = 0.5F;
		public float mainNoiseScaleX = 80.0F;
		public float mainNoiseScaleY = 160.0F;
		public float mainNoiseScaleZ = 80.0F;
		public float baseSize = 8.5F;
		public float stretchY = 12.0F;
		public float biomeDepthWeight = 1.0F;
		public float biomeDepthOffset;
		public float biomeScaleWeight = 1.0F;
		public float biomeScaleOffset;
		public int seaLevel = 63;
		public boolean useCaves = true;
		public boolean useDungeons = true;
		public int dungeonChance = 8;
		public boolean useStrongholds = true;
		public boolean useVillages = true;
		public boolean useMineShafts = true;
		public boolean useTemples = true;
		public boolean useMonuments = true;
		public boolean field_191076_A = true;
		public boolean useRavines = true;
		public boolean useWaterLakes = true;
		public int waterLakeChance = 4;
		public boolean useLavaLakes = true;
		public int lavaLakeChance = 80;
		public boolean useLavaOceans;
		public int fixedBiome = -1;
		public int biomeSize = 4;
		public int riverSize = 4;
		public int dirtSize = 33;
		public int dirtCount = 10;
		public int dirtMinHeight;
		public int dirtMaxHeight = 256;
		public int gravelSize = 33;
		public int gravelCount = 8;
		public int gravelMinHeight;
		public int gravelMaxHeight = 256;
		public int graniteSize = 33;
		public int graniteCount = 10;
		public int graniteMinHeight;
		public int graniteMaxHeight = 80;
		public int dioriteSize = 33;
		public int dioriteCount = 10;
		public int dioriteMinHeight;
		public int dioriteMaxHeight = 80;
		public int andesiteSize = 33;
		public int andesiteCount = 10;
		public int andesiteMinHeight;
		public int andesiteMaxHeight = 80;
		public int coalSize = 17;
		public int coalCount = 20;
		public int coalMinHeight;
		public int coalMaxHeight = 128;
		public int ironSize = 9;
		public int ironCount = 20;
		public int ironMinHeight;
		public int ironMaxHeight = 64;
		public int goldSize = 9;
		public int goldCount = 2;
		public int goldMinHeight;
		public int goldMaxHeight = 32;
		public int redstoneSize = 8;
		public int redstoneCount = 8;
		public int redstoneMinHeight;
		public int redstoneMaxHeight = 16;
		public int diamondSize = 8;
		public int diamondCount = 1;
		public int diamondMinHeight;
		public int diamondMaxHeight = 16;
		public int lapisSize = 7;
		public int lapisCount = 1;
		public int lapisCenterHeight = 16;
		public int lapisSpread = 16;

		public static ChunkGeneratorSettings.Factory jsonToFactory(String p_177865_0_) {
			if (p_177865_0_.isEmpty()) {
				return new ChunkGeneratorSettings.Factory();
			} else {
				try {
					return (ChunkGeneratorSettings.Factory) JSONTypeProvider.deserialize(p_177865_0_,
							ChunkGeneratorSettings.Factory.class);
				} catch (Exception var2) {
					return new ChunkGeneratorSettings.Factory();
				}
			}
		}

		public String toString() {
			return JSONTypeProvider.serialize(this).toString();
		}

		public Factory() {
			this.setDefaults();
		}

		public void setDefaults() {
			this.coordinateScale = 684.412F;
			this.heightScale = 684.412F;
			this.upperLimitScale = 512.0F;
			this.lowerLimitScale = 512.0F;
			this.depthNoiseScaleX = 200.0F;
			this.depthNoiseScaleZ = 200.0F;
			this.depthNoiseScaleExponent = 0.5F;
			this.mainNoiseScaleX = 80.0F;
			this.mainNoiseScaleY = 160.0F;
			this.mainNoiseScaleZ = 80.0F;
			this.baseSize = 8.5F;
			this.stretchY = 12.0F;
			this.biomeDepthWeight = 1.0F;
			this.biomeDepthOffset = 0.0F;
			this.biomeScaleWeight = 1.0F;
			this.biomeScaleOffset = 0.0F;
			this.seaLevel = 63;
			this.useCaves = true;
			this.useDungeons = true;
			this.dungeonChance = 8;
			this.useStrongholds = true;
			this.useVillages = true;
			this.useMineShafts = true;
			this.useTemples = true;
			this.useMonuments = true;
			this.field_191076_A = true;
			this.useRavines = true;
			this.useWaterLakes = true;
			this.waterLakeChance = 4;
			this.useLavaLakes = true;
			this.lavaLakeChance = 80;
			this.useLavaOceans = false;
			this.fixedBiome = -1;
			this.biomeSize = 4;
			this.riverSize = 4;
			this.dirtSize = 33;
			this.dirtCount = 10;
			this.dirtMinHeight = 0;
			this.dirtMaxHeight = 256;
			this.gravelSize = 33;
			this.gravelCount = 8;
			this.gravelMinHeight = 0;
			this.gravelMaxHeight = 256;
			this.graniteSize = 33;
			this.graniteCount = 10;
			this.graniteMinHeight = 0;
			this.graniteMaxHeight = 80;
			this.dioriteSize = 33;
			this.dioriteCount = 10;
			this.dioriteMinHeight = 0;
			this.dioriteMaxHeight = 80;
			this.andesiteSize = 33;
			this.andesiteCount = 10;
			this.andesiteMinHeight = 0;
			this.andesiteMaxHeight = 80;
			this.coalSize = 17;
			this.coalCount = 20;
			this.coalMinHeight = 0;
			this.coalMaxHeight = 128;
			this.ironSize = 9;
			this.ironCount = 20;
			this.ironMinHeight = 0;
			this.ironMaxHeight = 64;
			this.goldSize = 9;
			this.goldCount = 2;
			this.goldMinHeight = 0;
			this.goldMaxHeight = 32;
			this.redstoneSize = 8;
			this.redstoneCount = 8;
			this.redstoneMinHeight = 0;
			this.redstoneMaxHeight = 16;
			this.diamondSize = 8;
			this.diamondCount = 1;
			this.diamondMinHeight = 0;
			this.diamondMaxHeight = 16;
			this.lapisSize = 7;
			this.lapisCount = 1;
			this.lapisCenterHeight = 16;
			this.lapisSpread = 16;
		}

		public boolean equals(Object p_equals_1_) {
			if (this == p_equals_1_) {
				return true;
			} else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
				ChunkGeneratorSettings.Factory chunkgeneratorsettings$factory = (ChunkGeneratorSettings.Factory) p_equals_1_;

				if (this.andesiteCount != chunkgeneratorsettings$factory.andesiteCount) {
					return false;
				} else if (this.andesiteMaxHeight != chunkgeneratorsettings$factory.andesiteMaxHeight) {
					return false;
				} else if (this.andesiteMinHeight != chunkgeneratorsettings$factory.andesiteMinHeight) {
					return false;
				} else if (this.andesiteSize != chunkgeneratorsettings$factory.andesiteSize) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.baseSize, this.baseSize) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.biomeDepthOffset, this.biomeDepthOffset) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.biomeDepthWeight, this.biomeDepthWeight) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.biomeScaleOffset, this.biomeScaleOffset) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.biomeScaleWeight, this.biomeScaleWeight) != 0) {
					return false;
				} else if (this.biomeSize != chunkgeneratorsettings$factory.biomeSize) {
					return false;
				} else if (this.coalCount != chunkgeneratorsettings$factory.coalCount) {
					return false;
				} else if (this.coalMaxHeight != chunkgeneratorsettings$factory.coalMaxHeight) {
					return false;
				} else if (this.coalMinHeight != chunkgeneratorsettings$factory.coalMinHeight) {
					return false;
				} else if (this.coalSize != chunkgeneratorsettings$factory.coalSize) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.coordinateScale, this.coordinateScale) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.depthNoiseScaleExponent,
						this.depthNoiseScaleExponent) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.depthNoiseScaleX, this.depthNoiseScaleX) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.depthNoiseScaleZ, this.depthNoiseScaleZ) != 0) {
					return false;
				} else if (this.diamondCount != chunkgeneratorsettings$factory.diamondCount) {
					return false;
				} else if (this.diamondMaxHeight != chunkgeneratorsettings$factory.diamondMaxHeight) {
					return false;
				} else if (this.diamondMinHeight != chunkgeneratorsettings$factory.diamondMinHeight) {
					return false;
				} else if (this.diamondSize != chunkgeneratorsettings$factory.diamondSize) {
					return false;
				} else if (this.dioriteCount != chunkgeneratorsettings$factory.dioriteCount) {
					return false;
				} else if (this.dioriteMaxHeight != chunkgeneratorsettings$factory.dioriteMaxHeight) {
					return false;
				} else if (this.dioriteMinHeight != chunkgeneratorsettings$factory.dioriteMinHeight) {
					return false;
				} else if (this.dioriteSize != chunkgeneratorsettings$factory.dioriteSize) {
					return false;
				} else if (this.dirtCount != chunkgeneratorsettings$factory.dirtCount) {
					return false;
				} else if (this.dirtMaxHeight != chunkgeneratorsettings$factory.dirtMaxHeight) {
					return false;
				} else if (this.dirtMinHeight != chunkgeneratorsettings$factory.dirtMinHeight) {
					return false;
				} else if (this.dirtSize != chunkgeneratorsettings$factory.dirtSize) {
					return false;
				} else if (this.dungeonChance != chunkgeneratorsettings$factory.dungeonChance) {
					return false;
				} else if (this.fixedBiome != chunkgeneratorsettings$factory.fixedBiome) {
					return false;
				} else if (this.goldCount != chunkgeneratorsettings$factory.goldCount) {
					return false;
				} else if (this.goldMaxHeight != chunkgeneratorsettings$factory.goldMaxHeight) {
					return false;
				} else if (this.goldMinHeight != chunkgeneratorsettings$factory.goldMinHeight) {
					return false;
				} else if (this.goldSize != chunkgeneratorsettings$factory.goldSize) {
					return false;
				} else if (this.graniteCount != chunkgeneratorsettings$factory.graniteCount) {
					return false;
				} else if (this.graniteMaxHeight != chunkgeneratorsettings$factory.graniteMaxHeight) {
					return false;
				} else if (this.graniteMinHeight != chunkgeneratorsettings$factory.graniteMinHeight) {
					return false;
				} else if (this.graniteSize != chunkgeneratorsettings$factory.graniteSize) {
					return false;
				} else if (this.gravelCount != chunkgeneratorsettings$factory.gravelCount) {
					return false;
				} else if (this.gravelMaxHeight != chunkgeneratorsettings$factory.gravelMaxHeight) {
					return false;
				} else if (this.gravelMinHeight != chunkgeneratorsettings$factory.gravelMinHeight) {
					return false;
				} else if (this.gravelSize != chunkgeneratorsettings$factory.gravelSize) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.heightScale, this.heightScale) != 0) {
					return false;
				} else if (this.ironCount != chunkgeneratorsettings$factory.ironCount) {
					return false;
				} else if (this.ironMaxHeight != chunkgeneratorsettings$factory.ironMaxHeight) {
					return false;
				} else if (this.ironMinHeight != chunkgeneratorsettings$factory.ironMinHeight) {
					return false;
				} else if (this.ironSize != chunkgeneratorsettings$factory.ironSize) {
					return false;
				} else if (this.lapisCenterHeight != chunkgeneratorsettings$factory.lapisCenterHeight) {
					return false;
				} else if (this.lapisCount != chunkgeneratorsettings$factory.lapisCount) {
					return false;
				} else if (this.lapisSize != chunkgeneratorsettings$factory.lapisSize) {
					return false;
				} else if (this.lapisSpread != chunkgeneratorsettings$factory.lapisSpread) {
					return false;
				} else if (this.lavaLakeChance != chunkgeneratorsettings$factory.lavaLakeChance) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.lowerLimitScale, this.lowerLimitScale) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.mainNoiseScaleX, this.mainNoiseScaleX) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.mainNoiseScaleY, this.mainNoiseScaleY) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.mainNoiseScaleZ, this.mainNoiseScaleZ) != 0) {
					return false;
				} else if (this.redstoneCount != chunkgeneratorsettings$factory.redstoneCount) {
					return false;
				} else if (this.redstoneMaxHeight != chunkgeneratorsettings$factory.redstoneMaxHeight) {
					return false;
				} else if (this.redstoneMinHeight != chunkgeneratorsettings$factory.redstoneMinHeight) {
					return false;
				} else if (this.redstoneSize != chunkgeneratorsettings$factory.redstoneSize) {
					return false;
				} else if (this.riverSize != chunkgeneratorsettings$factory.riverSize) {
					return false;
				} else if (this.seaLevel != chunkgeneratorsettings$factory.seaLevel) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.stretchY, this.stretchY) != 0) {
					return false;
				} else if (Float.compare(chunkgeneratorsettings$factory.upperLimitScale, this.upperLimitScale) != 0) {
					return false;
				} else if (this.useCaves != chunkgeneratorsettings$factory.useCaves) {
					return false;
				} else if (this.useDungeons != chunkgeneratorsettings$factory.useDungeons) {
					return false;
				} else if (this.useLavaLakes != chunkgeneratorsettings$factory.useLavaLakes) {
					return false;
				} else if (this.useLavaOceans != chunkgeneratorsettings$factory.useLavaOceans) {
					return false;
				} else if (this.useMineShafts != chunkgeneratorsettings$factory.useMineShafts) {
					return false;
				} else if (this.useRavines != chunkgeneratorsettings$factory.useRavines) {
					return false;
				} else if (this.useStrongholds != chunkgeneratorsettings$factory.useStrongholds) {
					return false;
				} else if (this.useTemples != chunkgeneratorsettings$factory.useTemples) {
					return false;
				} else if (this.useMonuments != chunkgeneratorsettings$factory.useMonuments) {
					return false;
				} else if (this.field_191076_A != chunkgeneratorsettings$factory.field_191076_A) {
					return false;
				} else if (this.useVillages != chunkgeneratorsettings$factory.useVillages) {
					return false;
				} else if (this.useWaterLakes != chunkgeneratorsettings$factory.useWaterLakes) {
					return false;
				} else {
					return this.waterLakeChance == chunkgeneratorsettings$factory.waterLakeChance;
				}
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.coordinateScale == 0.0F ? 0 : Float.floatToIntBits(this.coordinateScale);
			i = 31 * i + (this.heightScale == 0.0F ? 0 : Float.floatToIntBits(this.heightScale));
			i = 31 * i + (this.upperLimitScale == 0.0F ? 0 : Float.floatToIntBits(this.upperLimitScale));
			i = 31 * i + (this.lowerLimitScale == 0.0F ? 0 : Float.floatToIntBits(this.lowerLimitScale));
			i = 31 * i + (this.depthNoiseScaleX == 0.0F ? 0 : Float.floatToIntBits(this.depthNoiseScaleX));
			i = 31 * i + (this.depthNoiseScaleZ == 0.0F ? 0 : Float.floatToIntBits(this.depthNoiseScaleZ));
			i = 31 * i
					+ (this.depthNoiseScaleExponent == 0.0F ? 0 : Float.floatToIntBits(this.depthNoiseScaleExponent));
			i = 31 * i + (this.mainNoiseScaleX == 0.0F ? 0 : Float.floatToIntBits(this.mainNoiseScaleX));
			i = 31 * i + (this.mainNoiseScaleY == 0.0F ? 0 : Float.floatToIntBits(this.mainNoiseScaleY));
			i = 31 * i + (this.mainNoiseScaleZ == 0.0F ? 0 : Float.floatToIntBits(this.mainNoiseScaleZ));
			i = 31 * i + (this.baseSize == 0.0F ? 0 : Float.floatToIntBits(this.baseSize));
			i = 31 * i + (this.stretchY == 0.0F ? 0 : Float.floatToIntBits(this.stretchY));
			i = 31 * i + (this.biomeDepthWeight == 0.0F ? 0 : Float.floatToIntBits(this.biomeDepthWeight));
			i = 31 * i + (this.biomeDepthOffset == 0.0F ? 0 : Float.floatToIntBits(this.biomeDepthOffset));
			i = 31 * i + (this.biomeScaleWeight == 0.0F ? 0 : Float.floatToIntBits(this.biomeScaleWeight));
			i = 31 * i + (this.biomeScaleOffset == 0.0F ? 0 : Float.floatToIntBits(this.biomeScaleOffset));
			i = 31 * i + this.seaLevel;
			i = 31 * i + (this.useCaves ? 1 : 0);
			i = 31 * i + (this.useDungeons ? 1 : 0);
			i = 31 * i + this.dungeonChance;
			i = 31 * i + (this.useStrongholds ? 1 : 0);
			i = 31 * i + (this.useVillages ? 1 : 0);
			i = 31 * i + (this.useMineShafts ? 1 : 0);
			i = 31 * i + (this.useTemples ? 1 : 0);
			i = 31 * i + (this.useMonuments ? 1 : 0);
			i = 31 * i + (this.field_191076_A ? 1 : 0);
			i = 31 * i + (this.useRavines ? 1 : 0);
			i = 31 * i + (this.useWaterLakes ? 1 : 0);
			i = 31 * i + this.waterLakeChance;
			i = 31 * i + (this.useLavaLakes ? 1 : 0);
			i = 31 * i + this.lavaLakeChance;
			i = 31 * i + (this.useLavaOceans ? 1 : 0);
			i = 31 * i + this.fixedBiome;
			i = 31 * i + this.biomeSize;
			i = 31 * i + this.riverSize;
			i = 31 * i + this.dirtSize;
			i = 31 * i + this.dirtCount;
			i = 31 * i + this.dirtMinHeight;
			i = 31 * i + this.dirtMaxHeight;
			i = 31 * i + this.gravelSize;
			i = 31 * i + this.gravelCount;
			i = 31 * i + this.gravelMinHeight;
			i = 31 * i + this.gravelMaxHeight;
			i = 31 * i + this.graniteSize;
			i = 31 * i + this.graniteCount;
			i = 31 * i + this.graniteMinHeight;
			i = 31 * i + this.graniteMaxHeight;
			i = 31 * i + this.dioriteSize;
			i = 31 * i + this.dioriteCount;
			i = 31 * i + this.dioriteMinHeight;
			i = 31 * i + this.dioriteMaxHeight;
			i = 31 * i + this.andesiteSize;
			i = 31 * i + this.andesiteCount;
			i = 31 * i + this.andesiteMinHeight;
			i = 31 * i + this.andesiteMaxHeight;
			i = 31 * i + this.coalSize;
			i = 31 * i + this.coalCount;
			i = 31 * i + this.coalMinHeight;
			i = 31 * i + this.coalMaxHeight;
			i = 31 * i + this.ironSize;
			i = 31 * i + this.ironCount;
			i = 31 * i + this.ironMinHeight;
			i = 31 * i + this.ironMaxHeight;
			i = 31 * i + this.goldSize;
			i = 31 * i + this.goldCount;
			i = 31 * i + this.goldMinHeight;
			i = 31 * i + this.goldMaxHeight;
			i = 31 * i + this.redstoneSize;
			i = 31 * i + this.redstoneCount;
			i = 31 * i + this.redstoneMinHeight;
			i = 31 * i + this.redstoneMaxHeight;
			i = 31 * i + this.diamondSize;
			i = 31 * i + this.diamondCount;
			i = 31 * i + this.diamondMinHeight;
			i = 31 * i + this.diamondMaxHeight;
			i = 31 * i + this.lapisSize;
			i = 31 * i + this.lapisCount;
			i = 31 * i + this.lapisCenterHeight;
			i = 31 * i + this.lapisSpread;
			return i;
		}

		public ChunkGeneratorSettings build() {
			return new ChunkGeneratorSettings(this);
		}
	}

	public static class Serializer implements JSONTypeCodec<ChunkGeneratorSettings.Factory, JSONObject>,
			JSONTypeDeserializer<JSONObject, ChunkGeneratorSettings.Factory> {
		public ChunkGeneratorSettings.Factory deserialize(JSONObject jsonobject) throws JSONException {
			ChunkGeneratorSettings.Factory chunkgeneratorsettings$factory = new ChunkGeneratorSettings.Factory();

			try {
				chunkgeneratorsettings$factory.coordinateScale = jsonobject.optFloat("coordinateScale",
						chunkgeneratorsettings$factory.coordinateScale);
				chunkgeneratorsettings$factory.heightScale = jsonobject.optFloat("heightScale",
						chunkgeneratorsettings$factory.heightScale);
				chunkgeneratorsettings$factory.lowerLimitScale = jsonobject.optFloat("lowerLimitScale",
						chunkgeneratorsettings$factory.lowerLimitScale);
				chunkgeneratorsettings$factory.upperLimitScale = jsonobject.optFloat("upperLimitScale",
						chunkgeneratorsettings$factory.upperLimitScale);
				chunkgeneratorsettings$factory.depthNoiseScaleX = jsonobject.optFloat("depthNoiseScaleX",
						chunkgeneratorsettings$factory.depthNoiseScaleX);
				chunkgeneratorsettings$factory.depthNoiseScaleZ = jsonobject.optFloat("depthNoiseScaleZ",
						chunkgeneratorsettings$factory.depthNoiseScaleZ);
				chunkgeneratorsettings$factory.depthNoiseScaleExponent = jsonobject.optFloat("depthNoiseScaleExponent",
						chunkgeneratorsettings$factory.depthNoiseScaleExponent);
				chunkgeneratorsettings$factory.mainNoiseScaleX = jsonobject.optFloat("mainNoiseScaleX",
						chunkgeneratorsettings$factory.mainNoiseScaleX);
				chunkgeneratorsettings$factory.mainNoiseScaleY = jsonobject.optFloat("mainNoiseScaleY",
						chunkgeneratorsettings$factory.mainNoiseScaleY);
				chunkgeneratorsettings$factory.mainNoiseScaleZ = jsonobject.optFloat("mainNoiseScaleZ",
						chunkgeneratorsettings$factory.mainNoiseScaleZ);
				chunkgeneratorsettings$factory.baseSize = jsonobject.optFloat("baseSize",
						chunkgeneratorsettings$factory.baseSize);
				chunkgeneratorsettings$factory.stretchY = jsonobject.optFloat("stretchY",
						chunkgeneratorsettings$factory.stretchY);
				chunkgeneratorsettings$factory.biomeDepthWeight = jsonobject.optFloat("biomeDepthWeight",
						chunkgeneratorsettings$factory.biomeDepthWeight);
				chunkgeneratorsettings$factory.biomeDepthOffset = jsonobject.optFloat("biomeDepthOffset",
						chunkgeneratorsettings$factory.biomeDepthOffset);
				chunkgeneratorsettings$factory.biomeScaleWeight = jsonobject.optFloat("biomeScaleWeight",
						chunkgeneratorsettings$factory.biomeScaleWeight);
				chunkgeneratorsettings$factory.biomeScaleOffset = jsonobject.optFloat("biomeScaleOffset",
						chunkgeneratorsettings$factory.biomeScaleOffset);
				chunkgeneratorsettings$factory.seaLevel = jsonobject.optInt("seaLevel",
						chunkgeneratorsettings$factory.seaLevel);
				chunkgeneratorsettings$factory.useCaves = jsonobject.optBoolean("useCaves",
						chunkgeneratorsettings$factory.useCaves);
				chunkgeneratorsettings$factory.useDungeons = jsonobject.optBoolean("useDungeons",
						chunkgeneratorsettings$factory.useDungeons);
				chunkgeneratorsettings$factory.dungeonChance = jsonobject.optInt("dungeonChance",
						chunkgeneratorsettings$factory.dungeonChance);
				chunkgeneratorsettings$factory.useStrongholds = jsonobject.optBoolean("useStrongholds",
						chunkgeneratorsettings$factory.useStrongholds);
				chunkgeneratorsettings$factory.useVillages = jsonobject.optBoolean("useVillages",
						chunkgeneratorsettings$factory.useVillages);
				chunkgeneratorsettings$factory.useMineShafts = jsonobject.optBoolean("useMineShafts",
						chunkgeneratorsettings$factory.useMineShafts);
				chunkgeneratorsettings$factory.useTemples = jsonobject.optBoolean("useTemples",
						chunkgeneratorsettings$factory.useTemples);
				chunkgeneratorsettings$factory.useMonuments = jsonobject.optBoolean("useMonuments",
						chunkgeneratorsettings$factory.useMonuments);
				chunkgeneratorsettings$factory.field_191076_A = jsonobject.optBoolean("useMansions",
						chunkgeneratorsettings$factory.field_191076_A);
				chunkgeneratorsettings$factory.useRavines = jsonobject.optBoolean("useRavines",
						chunkgeneratorsettings$factory.useRavines);
				chunkgeneratorsettings$factory.useWaterLakes = jsonobject.optBoolean("useWaterLakes",
						chunkgeneratorsettings$factory.useWaterLakes);
				chunkgeneratorsettings$factory.waterLakeChance = jsonobject.optInt("waterLakeChance",
						chunkgeneratorsettings$factory.waterLakeChance);
				chunkgeneratorsettings$factory.useLavaLakes = jsonobject.optBoolean("useLavaLakes",
						chunkgeneratorsettings$factory.useLavaLakes);
				chunkgeneratorsettings$factory.lavaLakeChance = jsonobject.optInt("lavaLakeChance",
						chunkgeneratorsettings$factory.lavaLakeChance);
				chunkgeneratorsettings$factory.useLavaOceans = jsonobject.optBoolean("useLavaOceans",
						chunkgeneratorsettings$factory.useLavaOceans);
				chunkgeneratorsettings$factory.fixedBiome = jsonobject.optInt("fixedBiome",
						chunkgeneratorsettings$factory.fixedBiome);

				if (chunkgeneratorsettings$factory.fixedBiome < 38 && chunkgeneratorsettings$factory.fixedBiome >= -1) {
					if (chunkgeneratorsettings$factory.fixedBiome >= Biome.getIdForBiome(Biomes.HELL)) {
						chunkgeneratorsettings$factory.fixedBiome += 2;
					}
				} else {
					chunkgeneratorsettings$factory.fixedBiome = -1;
				}

				chunkgeneratorsettings$factory.biomeSize = jsonobject.optInt("biomeSize",
						chunkgeneratorsettings$factory.biomeSize);
				chunkgeneratorsettings$factory.riverSize = jsonobject.optInt("riverSize",
						chunkgeneratorsettings$factory.riverSize);
				chunkgeneratorsettings$factory.dirtSize = jsonobject.optInt("dirtSize",
						chunkgeneratorsettings$factory.dirtSize);
				chunkgeneratorsettings$factory.dirtCount = jsonobject.optInt("dirtCount",
						chunkgeneratorsettings$factory.dirtCount);
				chunkgeneratorsettings$factory.dirtMinHeight = jsonobject.optInt("dirtMinHeight",
						chunkgeneratorsettings$factory.dirtMinHeight);
				chunkgeneratorsettings$factory.dirtMaxHeight = jsonobject.optInt("dirtMaxHeight",
						chunkgeneratorsettings$factory.dirtMaxHeight);
				chunkgeneratorsettings$factory.gravelSize = jsonobject.optInt("gravelSize",
						chunkgeneratorsettings$factory.gravelSize);
				chunkgeneratorsettings$factory.gravelCount = jsonobject.optInt("gravelCount",
						chunkgeneratorsettings$factory.gravelCount);
				chunkgeneratorsettings$factory.gravelMinHeight = jsonobject.optInt("gravelMinHeight",
						chunkgeneratorsettings$factory.gravelMinHeight);
				chunkgeneratorsettings$factory.gravelMaxHeight = jsonobject.optInt("gravelMaxHeight",
						chunkgeneratorsettings$factory.gravelMaxHeight);
				chunkgeneratorsettings$factory.graniteSize = jsonobject.optInt("graniteSize",
						chunkgeneratorsettings$factory.graniteSize);
				chunkgeneratorsettings$factory.graniteCount = jsonobject.optInt("graniteCount",
						chunkgeneratorsettings$factory.graniteCount);
				chunkgeneratorsettings$factory.graniteMinHeight = jsonobject.optInt("graniteMinHeight",
						chunkgeneratorsettings$factory.graniteMinHeight);
				chunkgeneratorsettings$factory.graniteMaxHeight = jsonobject.optInt("graniteMaxHeight",
						chunkgeneratorsettings$factory.graniteMaxHeight);
				chunkgeneratorsettings$factory.dioriteSize = jsonobject.optInt("dioriteSize",
						chunkgeneratorsettings$factory.dioriteSize);
				chunkgeneratorsettings$factory.dioriteCount = jsonobject.optInt("dioriteCount",
						chunkgeneratorsettings$factory.dioriteCount);
				chunkgeneratorsettings$factory.dioriteMinHeight = jsonobject.optInt("dioriteMinHeight",
						chunkgeneratorsettings$factory.dioriteMinHeight);
				chunkgeneratorsettings$factory.dioriteMaxHeight = jsonobject.optInt("dioriteMaxHeight",
						chunkgeneratorsettings$factory.dioriteMaxHeight);
				chunkgeneratorsettings$factory.andesiteSize = jsonobject.optInt("andesiteSize",
						chunkgeneratorsettings$factory.andesiteSize);
				chunkgeneratorsettings$factory.andesiteCount = jsonobject.optInt("andesiteCount",
						chunkgeneratorsettings$factory.andesiteCount);
				chunkgeneratorsettings$factory.andesiteMinHeight = jsonobject.optInt("andesiteMinHeight",
						chunkgeneratorsettings$factory.andesiteMinHeight);
				chunkgeneratorsettings$factory.andesiteMaxHeight = jsonobject.optInt("andesiteMaxHeight",
						chunkgeneratorsettings$factory.andesiteMaxHeight);
				chunkgeneratorsettings$factory.coalSize = jsonobject.optInt("coalSize",
						chunkgeneratorsettings$factory.coalSize);
				chunkgeneratorsettings$factory.coalCount = jsonobject.optInt("coalCount",
						chunkgeneratorsettings$factory.coalCount);
				chunkgeneratorsettings$factory.coalMinHeight = jsonobject.optInt("coalMinHeight",
						chunkgeneratorsettings$factory.coalMinHeight);
				chunkgeneratorsettings$factory.coalMaxHeight = jsonobject.optInt("coalMaxHeight",
						chunkgeneratorsettings$factory.coalMaxHeight);
				chunkgeneratorsettings$factory.ironSize = jsonobject.optInt("ironSize",
						chunkgeneratorsettings$factory.ironSize);
				chunkgeneratorsettings$factory.ironCount = jsonobject.optInt("ironCount",
						chunkgeneratorsettings$factory.ironCount);
				chunkgeneratorsettings$factory.ironMinHeight = jsonobject.optInt("ironMinHeight",
						chunkgeneratorsettings$factory.ironMinHeight);
				chunkgeneratorsettings$factory.ironMaxHeight = jsonobject.optInt("ironMaxHeight",
						chunkgeneratorsettings$factory.ironMaxHeight);
				chunkgeneratorsettings$factory.goldSize = jsonobject.optInt("goldSize",
						chunkgeneratorsettings$factory.goldSize);
				chunkgeneratorsettings$factory.goldCount = jsonobject.optInt("goldCount",
						chunkgeneratorsettings$factory.goldCount);
				chunkgeneratorsettings$factory.goldMinHeight = jsonobject.optInt("goldMinHeight",
						chunkgeneratorsettings$factory.goldMinHeight);
				chunkgeneratorsettings$factory.goldMaxHeight = jsonobject.optInt("goldMaxHeight",
						chunkgeneratorsettings$factory.goldMaxHeight);
				chunkgeneratorsettings$factory.redstoneSize = jsonobject.optInt("redstoneSize",
						chunkgeneratorsettings$factory.redstoneSize);
				chunkgeneratorsettings$factory.redstoneCount = jsonobject.optInt("redstoneCount",
						chunkgeneratorsettings$factory.redstoneCount);
				chunkgeneratorsettings$factory.redstoneMinHeight = jsonobject.optInt("redstoneMinHeight",
						chunkgeneratorsettings$factory.redstoneMinHeight);
				chunkgeneratorsettings$factory.redstoneMaxHeight = jsonobject.optInt("redstoneMaxHeight",
						chunkgeneratorsettings$factory.redstoneMaxHeight);
				chunkgeneratorsettings$factory.diamondSize = jsonobject.optInt("diamondSize",
						chunkgeneratorsettings$factory.diamondSize);
				chunkgeneratorsettings$factory.diamondCount = jsonobject.optInt("diamondCount",
						chunkgeneratorsettings$factory.diamondCount);
				chunkgeneratorsettings$factory.diamondMinHeight = jsonobject.optInt("diamondMinHeight",
						chunkgeneratorsettings$factory.diamondMinHeight);
				chunkgeneratorsettings$factory.diamondMaxHeight = jsonobject.optInt("diamondMaxHeight",
						chunkgeneratorsettings$factory.diamondMaxHeight);
				chunkgeneratorsettings$factory.lapisSize = jsonobject.optInt("lapisSize",
						chunkgeneratorsettings$factory.lapisSize);
				chunkgeneratorsettings$factory.lapisCount = jsonobject.optInt("lapisCount",
						chunkgeneratorsettings$factory.lapisCount);
				chunkgeneratorsettings$factory.lapisCenterHeight = jsonobject.optInt("lapisCenterHeight",
						chunkgeneratorsettings$factory.lapisCenterHeight);
				chunkgeneratorsettings$factory.lapisSpread = jsonobject.optInt("lapisSpread",
						chunkgeneratorsettings$factory.lapisSpread);
			} catch (Exception var7) {
				;
			}

			return chunkgeneratorsettings$factory;
		}

		public JSONObject serialize(ChunkGeneratorSettings.Factory p_serialize_1_) {
			JSONObject jsonobject = new JSONObject();
			jsonobject.put("coordinateScale", Float.valueOf(p_serialize_1_.coordinateScale));
			jsonobject.put("heightScale", Float.valueOf(p_serialize_1_.heightScale));
			jsonobject.put("lowerLimitScale", Float.valueOf(p_serialize_1_.lowerLimitScale));
			jsonobject.put("upperLimitScale", Float.valueOf(p_serialize_1_.upperLimitScale));
			jsonobject.put("depthNoiseScaleX", Float.valueOf(p_serialize_1_.depthNoiseScaleX));
			jsonobject.put("depthNoiseScaleZ", Float.valueOf(p_serialize_1_.depthNoiseScaleZ));
			jsonobject.put("depthNoiseScaleExponent", Float.valueOf(p_serialize_1_.depthNoiseScaleExponent));
			jsonobject.put("mainNoiseScaleX", Float.valueOf(p_serialize_1_.mainNoiseScaleX));
			jsonobject.put("mainNoiseScaleY", Float.valueOf(p_serialize_1_.mainNoiseScaleY));
			jsonobject.put("mainNoiseScaleZ", Float.valueOf(p_serialize_1_.mainNoiseScaleZ));
			jsonobject.put("baseSize", Float.valueOf(p_serialize_1_.baseSize));
			jsonobject.put("stretchY", Float.valueOf(p_serialize_1_.stretchY));
			jsonobject.put("biomeDepthWeight", Float.valueOf(p_serialize_1_.biomeDepthWeight));
			jsonobject.put("biomeDepthOffset", Float.valueOf(p_serialize_1_.biomeDepthOffset));
			jsonobject.put("biomeScaleWeight", Float.valueOf(p_serialize_1_.biomeScaleWeight));
			jsonobject.put("biomeScaleOffset", Float.valueOf(p_serialize_1_.biomeScaleOffset));
			jsonobject.put("seaLevel", Integer.valueOf(p_serialize_1_.seaLevel));
			jsonobject.put("useCaves", Boolean.valueOf(p_serialize_1_.useCaves));
			jsonobject.put("useDungeons", Boolean.valueOf(p_serialize_1_.useDungeons));
			jsonobject.put("dungeonChance", Integer.valueOf(p_serialize_1_.dungeonChance));
			jsonobject.put("useStrongholds", Boolean.valueOf(p_serialize_1_.useStrongholds));
			jsonobject.put("useVillages", Boolean.valueOf(p_serialize_1_.useVillages));
			jsonobject.put("useMineShafts", Boolean.valueOf(p_serialize_1_.useMineShafts));
			jsonobject.put("useTemples", Boolean.valueOf(p_serialize_1_.useTemples));
			jsonobject.put("useMonuments", Boolean.valueOf(p_serialize_1_.useMonuments));
			jsonobject.put("useMansions", Boolean.valueOf(p_serialize_1_.field_191076_A));
			jsonobject.put("useRavines", Boolean.valueOf(p_serialize_1_.useRavines));
			jsonobject.put("useWaterLakes", Boolean.valueOf(p_serialize_1_.useWaterLakes));
			jsonobject.put("waterLakeChance", Integer.valueOf(p_serialize_1_.waterLakeChance));
			jsonobject.put("useLavaLakes", Boolean.valueOf(p_serialize_1_.useLavaLakes));
			jsonobject.put("lavaLakeChance", Integer.valueOf(p_serialize_1_.lavaLakeChance));
			jsonobject.put("useLavaOceans", Boolean.valueOf(p_serialize_1_.useLavaOceans));
			jsonobject.put("fixedBiome", Integer.valueOf(p_serialize_1_.fixedBiome));
			jsonobject.put("biomeSize", Integer.valueOf(p_serialize_1_.biomeSize));
			jsonobject.put("riverSize", Integer.valueOf(p_serialize_1_.riverSize));
			jsonobject.put("dirtSize", Integer.valueOf(p_serialize_1_.dirtSize));
			jsonobject.put("dirtCount", Integer.valueOf(p_serialize_1_.dirtCount));
			jsonobject.put("dirtMinHeight", Integer.valueOf(p_serialize_1_.dirtMinHeight));
			jsonobject.put("dirtMaxHeight", Integer.valueOf(p_serialize_1_.dirtMaxHeight));
			jsonobject.put("gravelSize", Integer.valueOf(p_serialize_1_.gravelSize));
			jsonobject.put("gravelCount", Integer.valueOf(p_serialize_1_.gravelCount));
			jsonobject.put("gravelMinHeight", Integer.valueOf(p_serialize_1_.gravelMinHeight));
			jsonobject.put("gravelMaxHeight", Integer.valueOf(p_serialize_1_.gravelMaxHeight));
			jsonobject.put("graniteSize", Integer.valueOf(p_serialize_1_.graniteSize));
			jsonobject.put("graniteCount", Integer.valueOf(p_serialize_1_.graniteCount));
			jsonobject.put("graniteMinHeight", Integer.valueOf(p_serialize_1_.graniteMinHeight));
			jsonobject.put("graniteMaxHeight", Integer.valueOf(p_serialize_1_.graniteMaxHeight));
			jsonobject.put("dioriteSize", Integer.valueOf(p_serialize_1_.dioriteSize));
			jsonobject.put("dioriteCount", Integer.valueOf(p_serialize_1_.dioriteCount));
			jsonobject.put("dioriteMinHeight", Integer.valueOf(p_serialize_1_.dioriteMinHeight));
			jsonobject.put("dioriteMaxHeight", Integer.valueOf(p_serialize_1_.dioriteMaxHeight));
			jsonobject.put("andesiteSize", Integer.valueOf(p_serialize_1_.andesiteSize));
			jsonobject.put("andesiteCount", Integer.valueOf(p_serialize_1_.andesiteCount));
			jsonobject.put("andesiteMinHeight", Integer.valueOf(p_serialize_1_.andesiteMinHeight));
			jsonobject.put("andesiteMaxHeight", Integer.valueOf(p_serialize_1_.andesiteMaxHeight));
			jsonobject.put("coalSize", Integer.valueOf(p_serialize_1_.coalSize));
			jsonobject.put("coalCount", Integer.valueOf(p_serialize_1_.coalCount));
			jsonobject.put("coalMinHeight", Integer.valueOf(p_serialize_1_.coalMinHeight));
			jsonobject.put("coalMaxHeight", Integer.valueOf(p_serialize_1_.coalMaxHeight));
			jsonobject.put("ironSize", Integer.valueOf(p_serialize_1_.ironSize));
			jsonobject.put("ironCount", Integer.valueOf(p_serialize_1_.ironCount));
			jsonobject.put("ironMinHeight", Integer.valueOf(p_serialize_1_.ironMinHeight));
			jsonobject.put("ironMaxHeight", Integer.valueOf(p_serialize_1_.ironMaxHeight));
			jsonobject.put("goldSize", Integer.valueOf(p_serialize_1_.goldSize));
			jsonobject.put("goldCount", Integer.valueOf(p_serialize_1_.goldCount));
			jsonobject.put("goldMinHeight", Integer.valueOf(p_serialize_1_.goldMinHeight));
			jsonobject.put("goldMaxHeight", Integer.valueOf(p_serialize_1_.goldMaxHeight));
			jsonobject.put("redstoneSize", Integer.valueOf(p_serialize_1_.redstoneSize));
			jsonobject.put("redstoneCount", Integer.valueOf(p_serialize_1_.redstoneCount));
			jsonobject.put("redstoneMinHeight", Integer.valueOf(p_serialize_1_.redstoneMinHeight));
			jsonobject.put("redstoneMaxHeight", Integer.valueOf(p_serialize_1_.redstoneMaxHeight));
			jsonobject.put("diamondSize", Integer.valueOf(p_serialize_1_.diamondSize));
			jsonobject.put("diamondCount", Integer.valueOf(p_serialize_1_.diamondCount));
			jsonobject.put("diamondMinHeight", Integer.valueOf(p_serialize_1_.diamondMinHeight));
			jsonobject.put("diamondMaxHeight", Integer.valueOf(p_serialize_1_.diamondMaxHeight));
			jsonobject.put("lapisSize", Integer.valueOf(p_serialize_1_.lapisSize));
			jsonobject.put("lapisCount", Integer.valueOf(p_serialize_1_.lapisCount));
			jsonobject.put("lapisCenterHeight", Integer.valueOf(p_serialize_1_.lapisCenterHeight));
			jsonobject.put("lapisSpread", Integer.valueOf(p_serialize_1_.lapisSpread));
			return jsonobject;
		}
	}
}
