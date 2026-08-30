package net.minecraft.client.resources.data;

import org.json.JSONObject;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;

public class IMetadataSerializer {
	private final IRegistry metadataSectionSerializerRegistry = new RegistrySimple();
	private static final String __OBFID = "CL_00001101";

	public IMetadataSerializer() {
	}

	public void registerMetadataSectionType(IMetadataSectionSerializer p_110504_1_, Class p_110504_2_) {
		this.metadataSectionSerializerRegistry.putObject(p_110504_1_.getSectionName(),
				new IMetadataSerializer.Registration(p_110504_1_, p_110504_2_, null));
	}

	public IMetadataSection parseMetadataSection(String sectionName, JSONObject json) {
		if (sectionName == null) {
			throw new IllegalArgumentException("Metadata section name cannot be null");
		} else if (!json.has(sectionName)) {
			return null;
		} else if (json.optJSONObject(sectionName) == null) {
			throw new IllegalArgumentException(
					"Invalid metadata for '" + sectionName + "' - expected object, found " + json.get(sectionName));
		} else {
			IMetadataSerializer.Registration var3 = (IMetadataSerializer.Registration) this.metadataSectionSerializerRegistry
					.getObject(sectionName);
			if (var3 == null) {
				throw new IllegalArgumentException("Don't know how to handle metadata section '" + sectionName + "'");
			} else {
				return (IMetadataSection) JSONTypeProvider.deserialize(json.getJSONObject(sectionName),
						var3.field_110500_b);
			}
		}
	}

	class Registration {
		final IMetadataSectionSerializer field_110502_a;
		final Class field_110500_b;
		private static final String __OBFID = "CL_00001103";

		private Registration(IMetadataSectionSerializer p_i1305_2_, Class p_i1305_3_) {
			this.field_110502_a = p_i1305_2_;
			this.field_110500_b = p_i1305_3_;
		}

		Registration(IMetadataSectionSerializer p_i1306_2_, Class p_i1306_3_, Object p_i1306_4_) {
			this(p_i1306_2_, p_i1306_3_);
		}
	}
}
