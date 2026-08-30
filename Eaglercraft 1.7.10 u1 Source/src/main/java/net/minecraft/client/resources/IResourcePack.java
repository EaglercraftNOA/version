package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public interface IResourcePack {
	InputStream getInputStream(ResourceLocation p_110590_1_) throws IOException;

	boolean resourceExists(ResourceLocation p_110589_1_);

	Set<String> getResourceDomains();

	<T extends IMetadataSection> T getPackMetadata(IMetadataSerializer var1, String var2) throws IOException;

	ImageData getPackImage() throws IOException;

	String getPackName();
}
