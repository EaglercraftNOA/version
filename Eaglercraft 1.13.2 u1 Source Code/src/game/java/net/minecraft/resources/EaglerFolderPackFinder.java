package net.minecraft.resources;

import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerFolderResourcePack;

public class EaglerFolderPackFinder implements IPackFinder {
   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
      List<EaglerFolderResourcePack> list = EaglerFolderResourcePack.getFolderResourcePacks(EaglerFolderResourcePack.RESOURCE_PACKS);
      for(int i = 0, l = list.size(); i < l; ++i) {
         EaglerFolderResourcePack eaglerfolderresourcepack = list.get(i);
         String s = "folder/" + eaglerfolderresourcepack.getFolderName();
         T t = ResourcePackInfo.createResourcePack(s, false, () -> eaglerfolderresourcepack, packInfoFactory, ResourcePackInfo.Priority.TOP);
         if (t != null) {
            nameToPackMap.put(s, t);
         }
      }
   }
}
