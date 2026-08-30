package net.minecraft.client.resources;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class ResourcePackListEntryDefault extends ResourcePackListEntry {

	private static final Logger logger = LogManager.getLogger();
	private final IResourcePack field_148320_d;
	private final ResourceLocation resourcePackIcon;

	public ResourcePackListEntryDefault(GuiScreenResourcePacks resourcePacksGUIIn) {
		super(resourcePacksGUIIn);
		this.field_148320_d = this.mc.getResourcePackRepository().rprDefaultResourcePack;

		DynamicTexture dynamictexture;
		try {
			dynamictexture = new DynamicTexture(this.field_148320_d.getPackImage());
		} catch (IOException var4) {
			dynamictexture = TextureUtil.MISSING_TEXTURE;
		}

		this.resourcePackIcon = this.mc.getTextureManager().getDynamicTextureLocation("texturepackicon",
				dynamictexture);
	}

	@Override
	protected int getResourcePackFormat() {
		return 3;
	}

	@Override
	protected String getResourcePackDescription() {
		try {
			PackMetadataSection packmetadatasection = (PackMetadataSection) this.field_148320_d
					.getPackMetadata(this.mc.getResourcePackRepository().rprMetadataSerializer, "pack");
			if (packmetadatasection != null) {
				return packmetadatasection.getPackDescription().getFormattedText();
			}
		} catch (JSONException jsonparseexception) {
			logger.error("Couldn\'t load metadata info", jsonparseexception);
		} catch (IOException ioexception) {
			logger.error("Couldn\'t load metadata info", ioexception);
		}

		return TextFormatting.RED + "Missing " + "pack.mcmeta" + " :(";
	}

	@Override
	public boolean canMoveRight() {
		return false;
	}

	@Override
	public boolean canMoveLeft() {
		return false;
	}

	@Override
	public boolean canMoveUp() {
		return false;
	}

	@Override
	public boolean canMoveDown() {
		return false;
	}

	@Override
	public String getResourcePackName() {
		return "Default";
	}

	@Override
	public void bindResourcePackIcon() {
		this.mc.getTextureManager().bindTexture(this.resourcePackIcon);
	}

	@Override
	public boolean showHoverOverlay() {
		return false;
	}

	@Override
	protected String getEaglerFolderName() {
		return null;
	}
}
