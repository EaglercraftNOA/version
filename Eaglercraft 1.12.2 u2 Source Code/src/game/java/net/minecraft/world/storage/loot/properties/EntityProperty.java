package net.minecraft.world.storage.loot.properties;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public interface EntityProperty {
	boolean testProperty(EaglercraftRandom random, Entity entityIn);

	public abstract static class Serializer<T extends EntityProperty> {
		private final ResourceLocation name;
		private final Class<T> propertyClass;

		protected Serializer(ResourceLocation nameIn, Class<T> propertyClassIn) {
			this.name = nameIn;
			this.propertyClass = propertyClassIn;
		}

		public ResourceLocation getName() {
			return this.name;
		}

		public Class<T> getPropertyClass() {
			return this.propertyClass;
		}

		public abstract Object serialize(T property);

		public abstract T deserialize(Object element);
	}
}
