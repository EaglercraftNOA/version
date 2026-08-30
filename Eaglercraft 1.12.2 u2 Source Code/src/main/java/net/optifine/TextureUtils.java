package net.optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public class TextureUtils {
	
	public static String fixResourcePath(String p_fixResourcePath_0_, String p_fixResourcePath_1_) {
        String s = "assets/minecraft/";

        if (p_fixResourcePath_0_.startsWith(s)) {
            p_fixResourcePath_0_ = p_fixResourcePath_0_.substring(s.length());
            return p_fixResourcePath_0_;
        } else if (p_fixResourcePath_0_.startsWith("./")) {
            p_fixResourcePath_0_ = p_fixResourcePath_0_.substring(2);

            if (!p_fixResourcePath_1_.endsWith("/")) {
                p_fixResourcePath_1_ = p_fixResourcePath_1_ + "/";
            }

            p_fixResourcePath_0_ = p_fixResourcePath_1_ + p_fixResourcePath_0_;
            return p_fixResourcePath_0_;
        } else {
            if (p_fixResourcePath_0_.startsWith("/~")) {
                p_fixResourcePath_0_ = p_fixResourcePath_0_.substring(1);
            }

            String s1 = "mcpatcher/";

            if (p_fixResourcePath_0_.startsWith("~/")) {
                p_fixResourcePath_0_ = p_fixResourcePath_0_.substring(2);
                p_fixResourcePath_0_ = s1 + p_fixResourcePath_0_;
                return p_fixResourcePath_0_;
            } else if (p_fixResourcePath_0_.startsWith("/")) {
                p_fixResourcePath_0_ = s1 + p_fixResourcePath_0_.substring(1);
                return p_fixResourcePath_0_;
            } else {
                return p_fixResourcePath_0_;
            }
        }
    }
	
	public static void registerResourceListener() {
		IResourceManager iresourcemanager = Minecraft.getMinecraft().getResourceManager();
		if (iresourcemanager instanceof IReloadableResourceManager) {
			IReloadableResourceManager ireloadableresourcemanager = (IReloadableResourceManager) iresourcemanager;
			IResourceManagerReloadListener iresourcemanagerreloadlistener = new IResourceManagerReloadListener() {
				public void onResourceManagerReload(IResourceManager var1) {
					TextureUtils.resourcesReloaded(var1);
				}
			};
			ireloadableresourcemanager.registerReloadListener(iresourcemanagerreloadlistener);
		}
	}
	
	public static void resourcesReloaded(IResourceManager p_resourcesReloaded_0_) {
		if (getTextureMapBlocks() != null) {
			CustomItems.updateModels();
        }
	}
	
	public static TextureMap getTextureMapBlocks() {
		return Minecraft.getMinecraft().getTextureMapBlocks();
	}

}
