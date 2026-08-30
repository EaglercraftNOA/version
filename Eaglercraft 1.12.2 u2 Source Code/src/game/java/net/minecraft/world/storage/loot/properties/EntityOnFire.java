package net.minecraft.world.storage.loot.properties;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityOnFire implements EntityProperty {
	private final boolean onFire;

	public EntityOnFire(boolean onFireIn) {
		this.onFire = onFireIn;
	}

	public boolean testProperty(EaglercraftRandom random, Entity entityIn) {
		return entityIn.isBurning() == this.onFire;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityOnFire> {
		protected Serializer() {
			super(new ResourceLocation("on_fire"), EntityOnFire.class);
		}

		public Object serialize(EntityOnFire property) {
			return Boolean.valueOf(property.onFire);
		}

		public EntityOnFire deserialize(Object element) {
			return new EntityOnFire((Boolean) element);
		}
	}
}
