package net.minecraft.resources;

import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerFolderResourcePack;

public class EaglerFolderDataPackFinder implements IPackFinder {
   public static final String DATA_PACKS = "datapacks";

   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
      List<EaglerFolderResourcePack> list = EaglerFolderResourcePack.getFolderResourcePacks(DATA_PACKS);
      for(int i = 0, l = list.size(); i < l; ++i) {
         EaglerFolderResourcePack eaglerfolderresourcepack = list.get(i);
         String s = "datapack/" + eaglerfolderresourcepack.getFolderName();
         T t = ResourcePackInfo.createResourcePack(s, false, () -> eaglerfolderresourcepack, packInfoFactory, ResourcePackInfo.Priority.TOP);
         if (t != null) {
            nameToPackMap.put(s, t);
         }
      }
   }
}
