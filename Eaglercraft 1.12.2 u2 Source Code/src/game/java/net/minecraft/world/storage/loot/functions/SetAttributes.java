package net.minecraft.world.storage.loot.functions;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.peyton.eagler.json.JSONUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetAttributes extends LootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final SetAttributes.Modifier[] modifiers;

	public SetAttributes(LootCondition[] conditionsIn, SetAttributes.Modifier[] modifiersIn) {
		super(conditionsIn);
		this.modifiers = modifiersIn;
	}

	public ItemStack apply(ItemStack stack, EaglercraftRandom rand, LootContext context) {
		for (SetAttributes.Modifier setattributes$modifier : this.modifiers) {
			EaglercraftUUID uuid = setattributes$modifier.uuid;

			if (uuid == null) {
				uuid = EaglercraftUUID.randomUUID();
			}

			EntityEquipmentSlot entityequipmentslot = setattributes$modifier.slots[rand
					.nextInt(setattributes$modifier.slots.length)];
			stack.addAttributeModifier(setattributes$modifier.attributeName,
					new AttributeModifier(uuid, setattributes$modifier.modifierName,
							(double) setattributes$modifier.amount.generateFloat(rand),
							setattributes$modifier.operation),
					entityequipmentslot);
		}

		return stack;
	}

	static class Modifier {
		private final String modifierName;
		private final String attributeName;
		private final int operation;
		private final RandomValueRange amount;
		@Nullable
		private final EaglercraftUUID uuid;
		private final EntityEquipmentSlot[] slots;

		private Modifier(String modifName, String attrName, int operationIn, RandomValueRange randomAmount,
				EntityEquipmentSlot[] slotsIn, @Nullable EaglercraftUUID uuidIn) {
			this.modifierName = modifName;
			this.attributeName = attrName;
			this.operation = operationIn;
			this.amount = randomAmount;
			this.uuid = uuidIn;
			this.slots = slotsIn;
		}

		public JSONObject serialize() {
			JSONObject jsonobject = new JSONObject();
			jsonobject.put("name", this.modifierName);
			jsonobject.put("attribute", this.attributeName);
			jsonobject.put("operation", getOperationFromStr(this.operation));
			jsonobject.put("amount", (Object) JSONTypeProvider.serialize(this.amount));

			if (this.uuid != null) {
				jsonobject.put("id", this.uuid.toString());
			}

			if (this.slots.length == 1) {
				jsonobject.put("slot", this.slots[0].getName());
			} else {
				JSONArray jsonarray = new JSONArray();

				for (EntityEquipmentSlot entityequipmentslot : this.slots) {
					jsonarray.put(new String(entityequipmentslot.getName()));
				}

				jsonobject.put("slot", jsonarray);
			}

			return jsonobject;
		}

		public static SetAttributes.Modifier deserialize(JSONObject jsonObj) {
			String s = jsonObj.getString("name");
			String s1 = jsonObj.getString("attribute");
			int i = getOperationFromInt(jsonObj.getString("operation"));
			RandomValueRange randomvaluerange = (RandomValueRange) JSONTypeProvider.deserialize(jsonObj.get("amount"),
					RandomValueRange.class);
			EaglercraftUUID uuid = null;
			EntityEquipmentSlot[] aentityequipmentslot;

			if (jsonObj.get("slot") instanceof String) {
				aentityequipmentslot = new EntityEquipmentSlot[] {
						EntityEquipmentSlot.fromString(jsonObj.getString("slot")) };
			} else {
				if (!(jsonObj.get("slot") instanceof JSONArray)) {
					throw new JSONException(
							"Invalid or missing attribute modifier slot; must be either string or array of strings.");
				}

				JSONArray jsonarray = jsonObj.getJSONArray("slot");
				aentityequipmentslot = new EntityEquipmentSlot[jsonarray.length()];
				int j = 0;

				for (Object jsonelement : jsonarray) {
					aentityequipmentslot[j++] = EntityEquipmentSlot.fromString((String) jsonelement);
				}

				if (aentityequipmentslot.length == 0) {
					throw new JSONException("Invalid attribute modifier slot; must contain at least one entry.");
				}
			}

			if (jsonObj.has("id")) {
				String s2 = jsonObj.getString("id");

				try {
					uuid = EaglercraftUUID.fromString(s2);
				} catch (IllegalArgumentException var12) {
					throw new JSONException(
							"Invalid attribute modifier id '" + s2 + "' (must be UUID format, with dashes)");
				}
			}

			return new SetAttributes.Modifier(s, s1, i, randomvaluerange, aentityequipmentslot, uuid);
		}

		private static String getOperationFromStr(int operationIn) {
			switch (operationIn) {
			case 0:
				return "addition";

			case 1:
				return "multiply_base";

			case 2:
				return "multiply_total";

			default:
				throw new IllegalArgumentException("Unknown operation " + operationIn);
			}
		}

		private static int getOperationFromInt(String operationIn) {
			if ("addition".equals(operationIn)) {
				return 0;
			} else if ("multiply_base".equals(operationIn)) {
				return 1;
			} else if ("multiply_total".equals(operationIn)) {
				return 2;
			} else {
				throw new JSONException("Unknown attribute modifier operation " + operationIn);
			}
		}
	}

	public static class Serializer extends LootFunction.Serializer<SetAttributes> {
		public Serializer() {
			super(new ResourceLocation("set_attributes"), SetAttributes.class);
		}

		public void serialize(JSONObject object, SetAttributes functionClazz) {
			JSONArray jsonarray = new JSONArray();

			for (SetAttributes.Modifier setattributes$modifier : functionClazz.modifiers) {
				jsonarray.put(setattributes$modifier.serialize());
			}

			object.put("modifiers", jsonarray);
		}

		public SetAttributes deserialize(JSONObject object, LootCondition[] conditionsIn) {
			JSONArray jsonarray = object.getJSONArray("modifiers");
			SetAttributes.Modifier[] asetattributes$modifier = new SetAttributes.Modifier[jsonarray.length()];
			int i = 0;

			for (Object jsonelement : jsonarray) {
				asetattributes$modifier[i++] = SetAttributes.Modifier.deserialize((JSONObject) jsonelement);
			}

			if (asetattributes$modifier.length == 0) {
				throw new JSONException("Invalid attribute modifiers array; cannot be empty");
			} else {
				return new SetAttributes(conditionsIn, asetattributes$modifier);
			}
		}
	}
}
