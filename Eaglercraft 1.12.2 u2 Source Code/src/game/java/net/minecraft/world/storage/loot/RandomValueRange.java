package net.minecraft.world.storage.loot;

import org.json.JSONObject;
import org.json.JSONException;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.minecraft.util.math.MathHelper;

public class RandomValueRange {
	private final float min;
	private final float max;

	public RandomValueRange(float minIn, float maxIn) {
		this.min = minIn;
		this.max = maxIn;
	}

	public RandomValueRange(float value) {
		this.min = value;
		this.max = value;
	}

	public float getMin() {
		return this.min;
	}

	public float getMax() {
		return this.max;
	}

	public int generateInt(EaglercraftRandom rand) {
		return MathHelper.getInt(rand, MathHelper.floor(this.min), MathHelper.floor(this.max));
	}

	public float generateFloat(EaglercraftRandom rand) {
		return MathHelper.nextFloat(rand, this.min, this.max);
	}

	public boolean isInRange(int value) {
		return (float) value <= this.max && (float) value >= this.min;
	}

	public static class Serializer
			implements JSONTypeDeserializer<Object, RandomValueRange>, JSONTypeCodec<RandomValueRange, Object> {
		public RandomValueRange deserialize(Object p_deserialize_1_) throws JSONException {
			if (p_deserialize_1_ instanceof Number) {
				return new RandomValueRange(((Number) p_deserialize_1_).floatValue());
			} else {
				JSONObject jsonobject = (JSONObject) p_deserialize_1_;
				float f = jsonobject.getFloat("min");
				float f1 = jsonobject.getFloat("max");
				return new RandomValueRange(f, f1);
			}
		}

		public Object serialize(RandomValueRange p_serialize_1_) {
			if (p_serialize_1_.min == p_serialize_1_.max) {
				return (Number) Float.valueOf(p_serialize_1_.min);
			} else {
				JSONObject jsonobject = new JSONObject();
				jsonobject.put("min", Float.valueOf(p_serialize_1_.min));
				jsonobject.put("max", Float.valueOf(p_serialize_1_.max));
				return jsonobject;
			}
		}
	}
}
