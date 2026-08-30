package net.minecraft.client.resources.data;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.registry.RegistrySimple;

public class MetadataSerializer {
	private final IRegistry<String, MetadataSerializer.Registration<? extends IMetadataSection>> metadataSectionSerializerRegistry = new RegistrySimple<String, MetadataSerializer.Registration<? extends IMetadataSection>>();

	public <T extends IMetadataSection> void registerMetadataSectionType(
			IMetadataSectionSerializer<T> metadataSectionSerializer, Class<T> clazz) {
		this.metadataSectionSerializerRegistry.putObject(metadataSectionSerializer.getSectionName(),
				new MetadataSerializer.Registration(metadataSectionSerializer, clazz));
	}

	public <T extends IMetadataSection> T parseMetadataSection(String sectionName, JSONObject json) {
		if (sectionName == null) {
			throw new IllegalArgumentException("Metadata section name cannot be null");
		} else if (!json.has(sectionName)) {
			return (T) null;
		} else if (json.optJSONObject(sectionName) == null) {
			throw new IllegalArgumentException(
					"Invalid metadata for '" + sectionName + "' - expected object, found " + json.get(sectionName));
		} else {
			MetadataSerializer.Registration<?> registration = (MetadataSerializer.Registration) this.metadataSectionSerializerRegistry
					.getObject(sectionName);

			if (registration == null) {
				throw new IllegalArgumentException("Don't know how to handle metadata section '" + sectionName + "'");
			} else {
				return (T) ((IMetadataSection) JSONTypeProvider.deserialize(json.getJSONObject(sectionName),
						registration.clazz));
			}
		}
	}

	class Registration<T extends IMetadataSection> {
		final IMetadataSectionSerializer<T> section;
		final Class<T> clazz;

		private Registration(IMetadataSectionSerializer<T> metadataSectionSerializer, Class<T> clazzToRegister) {
			this.section = metadataSectionSerializer;
			this.clazz = clazzToRegister;
		}
	}
}
