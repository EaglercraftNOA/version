package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemOverride {
	private final ResourceLocation location;
	private final Map<ResourceLocation, Float> mapResourceValues;

	public ItemOverride(ResourceLocation locationIn, Map<ResourceLocation, Float> propertyValues) {
		this.location = locationIn;
		this.mapResourceValues = propertyValues;
	}

	/**
	 * Get the location of the target model
	 */
	public ResourceLocation getLocation() {
		return this.location;
	}

	boolean matchesItemStack(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingEntity) {
		Item item = stack.getItem();

		for (Entry<ResourceLocation, Float> entry : this.mapResourceValues.entrySet()) {
			IItemPropertyGetter iitempropertygetter = item.getPropertyGetter(entry.getKey());

			if (iitempropertygetter == null || iitempropertygetter.apply(stack, worldIn,
					livingEntity) < ((Float) entry.getValue()).floatValue()) {
				return false;
			}
		}

		return true;
	}

	public static class Deserializer implements JSONTypeDeserializer<JSONObject, ItemOverride> {
		public ItemOverride deserialize(JSONObject jsonobject) throws JSONException {
			ResourceLocation resourcelocation = new ResourceLocation(jsonobject.getString("model"));
			Map<ResourceLocation, Float> map = this.makeMapResourceValues(jsonobject);
			return new ItemOverride(resourcelocation, map);
		}

		protected Map<ResourceLocation, Float> makeMapResourceValues(JSONObject p_188025_1_) {
			Map<ResourceLocation, Float> map = Maps.<ResourceLocation, Float>newLinkedHashMap();
			JSONObject jsonobject = p_188025_1_.getJSONObject("predicate");

			for (String entry : jsonobject.keySet()) {
				map.put(new ResourceLocation(entry), Float.valueOf(jsonobject.getFloat(entry)));
			}

			return map;
		}
	}
}
