package net.minecraft.client.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.minecraft.ResourceIndex;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public interface IResourcePack {
	InputStream getInputStream(ResourceLocation location) throws IOException;

	boolean resourceExists(ResourceLocation location);

	Set<String> getResourceDomains();

	@Nullable
	<T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName)
			throws IOException;

	ImageData getPackImage() throws IOException;

	String getPackName();
	
	ResourceIndex getEaglerFileIndex();
}
