package net.minecraft.client.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.HString;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.minecraft.ResourceIndex;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractResourcePack implements IResourcePack {
	private static final Logger LOGGER = LogManager.getLogger();
	public final String resourcePackFile;
	protected ResourceIndex resourceIndex;

	public AbstractResourcePack(String resourcePackFileIn) {
		this.resourcePackFile = resourcePackFileIn;
	}

	private static String locationToName(ResourceLocation location) {
		return HString.format("%s/%s/%s",
				new Object[] { "assets", location.getResourceDomain(), location.getResourcePath() });
	}

	public InputStream getInputStream(ResourceLocation location) throws IOException {
		return this.getInputStreamByName(locationToName(location));
	}

	public boolean resourceExists(ResourceLocation location) {
		return this.hasResourceName(locationToName(location));
	}

	protected abstract InputStream getInputStreamByName(String name) throws IOException;

	protected abstract boolean hasResourceName(String name);

	protected void logNameNotLowercase(String name) {
		LOGGER.warn("ResourcePack: ignored non-lowercase namespace: %s in %s",
				new Object[] { name, this.resourcePackFile });
	}

	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer,
			String metadataSectionName) throws IOException {
		return (T) readMetadata(metadataSerializer, this.getInputStreamByName("pack.mcmeta"), metadataSectionName);
	}

	static <T extends IMetadataSection> T readMetadata(MetadataSerializer metadataSerializer, InputStream p_110596_1_,
			String sectionName) {
		JSONObject jsonobject = null;

		try {
			jsonobject = new JSONObject(IOUtils.inputStreamToString(p_110596_1_, StandardCharsets.UTF_8));
		} catch (RuntimeException | IOException runtimeexception) {
			throw new JSONException(runtimeexception);
		} finally {
			IOUtils.closeQuietly(p_110596_1_);
		}

		return (T) metadataSerializer.parseMetadataSection(sectionName, jsonobject);
	}

	public ImageData getPackImage() throws IOException {
		return TextureUtil.readBufferedImage(this.getInputStreamByName("pack.png"));
	}

	public String getPackName() {
		return this.resourcePackFile;
	}
	
	@Override
	public ResourceIndex getEaglerFileIndex() {
		return this.resourceIndex;
	}
}
