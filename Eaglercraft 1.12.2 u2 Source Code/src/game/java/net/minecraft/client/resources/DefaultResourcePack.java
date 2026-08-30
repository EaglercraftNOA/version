package net.minecraft.client.resources;

import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.minecraft.EaglerFolderResourcePack;
import net.lax1dude.eaglercraft.minecraft.ResourceIndex;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class DefaultResourcePack extends ResourceIndex implements IResourcePack {
	public static final Set<String> DEFAULT_RESOURCE_DOMAINS = ImmutableSet.<String>of("minecraft", "eagler", "optifine");
	
	private final Collection<String> propertyFilesIndex;

	public DefaultResourcePack() {
		String str = EagRuntime.getResourceString("/assets/minecraft/optifine/_property_files_index.json");
		if (str != null) {
			Collection<String> lst = EaglerFolderResourcePack.loadPropertyFileList(str);
			if (lst != null) {
				propertyFilesIndex = lst;
				return;
			}
		}
		propertyFilesIndex = Collections.emptyList();
	}

	public InputStream getInputStream(ResourceLocation location) throws IOException {
		InputStream inputstream = this.getInputStreamAssets(location);

		if (inputstream != null) {
			return inputstream;
		} else {
			InputStream inputstream1 = this.getResourceStream(location);

			if (inputstream1 != null) {
				return inputstream1;
			} else {
				throw new FileNotFoundException(location.getResourcePath());
			}
		}
	}

	@Nullable
	public InputStream getInputStreamAssets(ResourceLocation location) throws FileNotFoundException {
		return null;
	}

	@Nullable
	private InputStream getResourceStream(ResourceLocation location) {
		return EagRuntime
				.getResourceStream("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath());
	}

	public boolean resourceExists(ResourceLocation location) {
		return this.getResourceStream(location) != null;
	}

	public Set<String> getResourceDomains() {
		return DEFAULT_RESOURCE_DOMAINS;
	}

	@Nullable
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer,
			String metadataSectionName) throws IOException {
		try {
			return AbstractResourcePack.readMetadata(metadataSerializer, EagRuntime.getResourceStream("pack.mcmeta"),
					metadataSectionName);
		} catch (RuntimeException var4) {
			return (T) null;
		}
	}

	public ImageData getPackImage() throws IOException {
		return TextureUtil.readBufferedImage(EagRuntime.getResourceStream("pack.png"));
	}

	public String getPackName() {
		return "Default";
	}

	@Override
	public ResourceIndex getEaglerFileIndex() {
		return this;
	}
	
	@Override
	protected Collection<String> getPropertiesFiles0() {
		return propertyFilesIndex;
	}

	@Override
	protected Collection<String> getCITPotionsFiles0() {
		return Collections.emptyList();
	}
}
