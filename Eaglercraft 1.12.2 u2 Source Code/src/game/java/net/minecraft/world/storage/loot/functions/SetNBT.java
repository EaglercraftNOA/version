package net.minecraft.world.storage.loot.functions;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class SetNBT extends LootFunction {
	private final NBTTagCompound tag;

	public SetNBT(LootCondition[] conditionsIn, NBTTagCompound tagIn) {
		super(conditionsIn);
		this.tag = tagIn;
	}

	public ItemStack apply(ItemStack stack, EaglercraftRandom rand, LootContext context) {
		NBTTagCompound nbttagcompound = stack.getTagCompound();

		if (nbttagcompound == null) {
			nbttagcompound = this.tag.copy();
		} else {
			nbttagcompound.merge(this.tag);
		}

		stack.setTagCompound(nbttagcompound);
		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<SetNBT> {
		public Serializer() {
			super(new ResourceLocation("set_nbt"), SetNBT.class);
		}

		public void serialize(JSONObject object, SetNBT functionClazz) {
			object.put("tag", functionClazz.tag.toString());
		}

		public SetNBT deserialize(JSONObject object, LootCondition[] conditionsIn) {
			try {
				NBTTagCompound nbttagcompound = JsonToNBT.getTagFromJson(object.getString("tag"));
				return new SetNBT(conditionsIn, nbttagcompound);
			} catch (NBTException nbtexception) {
				throw new JSONException(nbtexception);
			}
		}
	}
}
