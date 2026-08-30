package net.minecraft.resources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerFolderResourcePack;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;

public class EaglerWorldDataPackFinder implements IPackFinder {
   public static final String DATA_PACKS = "datapacks";
   private static final String DATA_DIR = "data/";
   private final VFile2 datapacksFolder;

   public EaglerWorldDataPackFinder(VFile2 worldDirectory) {
      this.datapacksFolder = WorldsDB.newVFile(worldDirectory, DATA_PACKS);
   }

   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
      String basePath = datapacksFolder.getPath();
      if(basePath == null) {
         return;
      }
      int prefixLen = basePath.length() + 1;
      Set<String> folderNames = new HashSet<>();
      Map<String, Set<String>> folderDomains = new HashMap<>();
      List<String> filenames = datapacksFolder.listFilenames(true);
      for(int i = 0, l = filenames.size(); i < l; ++i) {
         String name = filenames.get(i);
         if(name.length() <= prefixLen) {
            continue;
         }
         String rel = name.substring(prefixLen);
         int j = rel.indexOf('/');
         if(j <= 0 || rel.charAt(0) == '.') {
            continue;
         }
         String folderName = rel.substring(0, j);
         String sub = rel.substring(j + 1);
         if(sub.equals("pack.mcmeta")) {
            folderNames.add(folderName);
         }else if(sub.startsWith(DATA_DIR)) {
            int k = sub.indexOf('/', DATA_DIR.length());
            if(k > DATA_DIR.length()) {
               Set<String> domains = folderDomains.get(folderName);
               if(domains == null) {
                  domains = new HashSet<>();
                  folderDomains.put(folderName, domains);
               }
               domains.add(sub.substring(DATA_DIR.length(), k));
            }
         }
      }

      for(String folderName : folderNames) {
         Set<String> domains = folderDomains.get(folderName);
         if(domains == null) {
            domains = new HashSet<>();
         }
         EaglerFolderResourcePack eaglerfolderresourcepack = new EaglerFolderResourcePack(
               WorldsDB.newVFile(datapacksFolder, folderName), folderName, domains, 0L);
         String s = "datapack/" + folderName;
         T t = ResourcePackInfo.createResourcePack(s, false, () -> eaglerfolderresourcepack, packInfoFactory, ResourcePackInfo.Priority.TOP);
         if (t != null) {
            nameToPackMap.put(s, t);
         }
      }
   }
}
