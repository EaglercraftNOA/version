package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.futures.Futures;
import net.lax1dude.eaglercraft.futures.ListenableFuture;
import net.lax1dude.eaglercraft.minecraft.EaglerFolderResourcePack;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
	private static final ResourceLocation field_191400_f = new ResourceLocation("textures/misc/unknown_pack.png");
	public final IResourcePack rprDefaultResourcePack;
	public final MetadataSerializer rprMetadataSerializer;
	private IResourcePack resourcePackInstance;
	private List<ResourcePackRepository.Entry> repositoryEntriesAll = Lists.<ResourcePackRepository.Entry>newArrayList();
	public final List<ResourcePackRepository.Entry> repositoryEntries = Lists.<ResourcePackRepository.Entry>newArrayList();

	public ResourcePackRepository(IResourcePack rprDefaultResourcePackIn, MetadataSerializer rprMetadataSerializerIn,
			GameSettings settings) {
		this.rprDefaultResourcePack = rprDefaultResourcePackIn;
		this.rprMetadataSerializer = rprMetadataSerializerIn;
		this.updateRepositoryEntriesAll();
		Iterator<String> iterator = settings.resourcePacks.iterator();

		while (iterator.hasNext()) {
			String s = iterator.next();

			for (ResourcePackRepository.Entry resourcepackrepository$entry : this.repositoryEntriesAll) {
				if (resourcepackrepository$entry.getResourcePackName().equals(s)) {
					if (resourcepackrepository$entry.getPackFormat() == 3 || settings.incompatibleResourcePacks
							.contains(resourcepackrepository$entry.getResourcePackName())) {
						this.repositoryEntries.add(resourcepackrepository$entry);
						break;
					}

					iterator.remove();
					LOGGER.warn("Removed selected resource pack {} because it's no longer compatible",
							(Object) resourcepackrepository$entry.getResourcePackName());
				}
			}
		}
	}

	public void updateRepositoryEntriesAll() {
		List<ResourcePackRepository.Entry> list = Lists.<ResourcePackRepository.Entry>newArrayList();

		List<EaglerFolderResourcePack> list2 = EaglerFolderResourcePack
				.getFolderResourcePacks(EaglerFolderResourcePack.RESOURCE_PACKS);
		for (int j = 0, l = list2.size(); j < l; ++j) {
			ResourcePackRepository.Entry resourcepackrepository$entry = new ResourcePackRepository.Entry(list2.get(j));

			if (!this.repositoryEntriesAll.contains(resourcepackrepository$entry)) {
				try {
					resourcepackrepository$entry.updateResourcePack();
					list.add(resourcepackrepository$entry);
				} catch (Exception var6) {
					LOGGER.error("Failed to call \"updateResourcePack\" for resource pack \"{}\"",
							resourcepackrepository$entry.reResourcePack.resourcePackFile);
					LOGGER.error(var6);
					list.remove(resourcepackrepository$entry);
				}
			} else {
				int i = this.repositoryEntriesAll.indexOf(resourcepackrepository$entry);

				if (i > -1 && i < this.repositoryEntriesAll.size()) {
					list.add(this.repositoryEntriesAll.get(i));
				}
			}
		}

		this.repositoryEntriesAll.removeAll(list);

		for (int i = 0, l = this.repositoryEntriesAll.size(); i < l; ++i) {
			this.repositoryEntriesAll.get(i).closeResourcePack();
		}

		this.repositoryEntriesAll = list;
	}

	public List<ResourcePackRepository.Entry> getRepositoryEntriesAll() {
		return ImmutableList.copyOf(this.repositoryEntriesAll);
	}

	public List<ResourcePackRepository.Entry> getRepositoryEntries() {
		return ImmutableList.copyOf(this.repositoryEntries);
	}

	public void setRepositories(List<ResourcePackRepository.Entry> repositories) {
		this.repositoryEntries.clear();
		this.repositoryEntries.addAll(repositories);
	}

	public void downloadResourcePack(String s1, String s2, Consumer<Boolean> cb) {
		EaglerFolderResourcePack.loadRemoteResourcePack(s1, s2, res -> {
			if (res != null) {
				ResourcePackRepository.this.resourcePackInstance = res;
				Minecraft.getMinecraft().scheduleResourcesRefresh();
				cb.accept(true);
				return;
			}
			cb.accept(false);
		}, runnable -> {
			Minecraft.getMinecraft().addScheduledTask(runnable);
		}, () -> {
			Minecraft.getMinecraft().loadingScreen.eaglerShow(I18n.format("resourcePack.load.loading"),
					"Server resource pack");
		});
	}

	@Nullable

	/**
	 * Getter for the IResourcePack instance associated with this
	 * ResourcePackRepository
	 */
	public IResourcePack getResourcePackInstance() {
		return this.resourcePackInstance;
	}

	public void clearResourcePack() {
		if (this.resourcePackInstance != null) {
			this.resourcePackInstance = null;
			Minecraft.getMinecraft().scheduleResourcesRefresh();
		}
	}

	public class Entry {
		private final EaglerFolderResourcePack reResourcePack;
		private PackMetadataSection rePackMetadataSection;
		private ImageData texturePackIcon;
		private ResourceLocation locationTexturePackIcon;
		private TextureManager iconTextureManager;

		private Entry(EaglerFolderResourcePack resourcePackFileIn) {
			this.reResourcePack = resourcePackFileIn;
		}

		public void updateResourcePack() throws IOException {
			this.rePackMetadataSection = (PackMetadataSection) this.reResourcePack
					.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");

			try {
				this.texturePackIcon = this.reResourcePack.getPackImage();
			} catch (Throwable var2) {
				LOGGER.error("Failed to load resource pack icon for \"{}\"!", reResourcePack.resourcePackFile);
				LOGGER.error(var2);
			}

			if (this.texturePackIcon == null) {
				this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
			}

			this.closeResourcePack();
		}

		public void bindTexturePackIcon(TextureManager textureManagerIn) {
			if (this.locationTexturePackIcon == null) {
				this.iconTextureManager = textureManagerIn;
				this.locationTexturePackIcon = textureManagerIn.getDynamicTextureLocation("texturepackicon",
						new DynamicTexture(this.texturePackIcon));
			}

			textureManagerIn.bindTexture(this.locationTexturePackIcon);
		}

		public void closeResourcePack() {
			if (this.locationTexturePackIcon != null) {
				this.iconTextureManager.deleteTexture(this.locationTexturePackIcon);
				this.locationTexturePackIcon = null;
			}
			if (this.reResourcePack instanceof Closeable) {
				IOUtils.closeQuietly((Closeable) this.reResourcePack);
			}
		}

		public IResourcePack getResourcePack() {
			return this.reResourcePack;
		}

		public String getResourcePackName() {
			return this.reResourcePack.getPackName();
		}

		public String getResourcePackEaglerDisplayName() {
			return this.reResourcePack.getDisplayName();
		}

		public String getTexturePackDescription() {
			return this.rePackMetadataSection == null
					? TextFormatting.RED + "Invalid pack.mcmeta (or missing 'pack' section)"
					: this.rePackMetadataSection.getPackDescription().getFormattedText();
		}

		public int getPackFormat() {
			return this.rePackMetadataSection == null ? 0 : this.rePackMetadataSection.getPackFormat();
		}

		public boolean equals(Object object) {
			return this == object ? true
					: (object instanceof ResourcePackRepository.Entry ? this.toString().equals(object.toString())
							: false);
		}

		public int hashCode() {
			return this.toString().hashCode();
		}

		public String toString() {
			return this.reResourcePack.resourcePackFile;
		}
	}
}
