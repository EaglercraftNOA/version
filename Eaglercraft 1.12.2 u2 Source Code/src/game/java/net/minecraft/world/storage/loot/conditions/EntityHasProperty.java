package net.minecraft.world.storage.loot.conditions;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;

import java.util.Set;

import org.json.JSONObject;

import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;
import net.peyton.eagler.json.JSONUtils;

public class EntityHasProperty implements LootCondition {
	private final EntityProperty[] properties;
	private final LootContext.EntityTarget target;

	public EntityHasProperty(EntityProperty[] propertiesIn, LootContext.EntityTarget targetIn) {
		this.properties = propertiesIn;
		this.target = targetIn;
	}

	public boolean testCondition(EaglercraftRandom rand, LootContext context) {
		Entity entity = context.getEntity(this.target);

		if (entity == null) {
			return false;
		} else {
			for (EntityProperty entityproperty : this.properties) {
				if (!entityproperty.testProperty(rand, entity)) {
					return false;
				}
			}

			return true;
		}
	}

	public static class Serializer extends LootCondition.Serializer<EntityHasProperty> {
		protected Serializer() {
			super(new ResourceLocation("entity_properties"), EntityHasProperty.class);
		}

		public void serialize(JSONObject json, EntityHasProperty value) {
			JSONObject jsonobject = new JSONObject();

			for (EntityProperty entityproperty : value.properties) {
				EntityProperty.Serializer<EntityProperty> serializer = EntityPropertyManager
						.<EntityProperty>getSerializerFor(entityproperty);
				jsonobject.put(serializer.getName().toString(), serializer.serialize(entityproperty));
			}

			json.put("properties", jsonobject);
			json.put("entity", (Object) JSONTypeProvider.serialize(value.target));
		}

		public EntityHasProperty deserialize(JSONObject json) {
			Set<Entry<String, Object>> set = json.getJSONObject("properties").entrySet();
			EntityProperty[] aentityproperty = new EntityProperty[set.size()];
			int i = 0;

			for (Entry<String, Object> entry : set) {
				aentityproperty[i++] = EntityPropertyManager.getSerializerForName(new ResourceLocation(entry.getKey()))
						.deserialize(entry.getValue());
			}

			return new EntityHasProperty(aentityproperty, (LootContext.EntityTarget) JSONTypeProvider
					.deserialize(json.get("entity"), LootContext.EntityTarget.class));
		}
	}
}
