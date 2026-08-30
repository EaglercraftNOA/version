package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.dimension.DimensionType;

public class WorldSavedDataStorage {
   private final Map<DimensionType, DimensionSavedDataManager> dataManagers;
   @Nullable
   private final ISaveHandler saveHandler;

   public WorldSavedDataStorage(@Nullable ISaveHandler saveHandlerIn) {
      this.saveHandler = saveHandlerIn;
      Builder<DimensionType, DimensionSavedDataManager> builder = ImmutableMap.builder();

      for(DimensionType dimensiontype : DimensionType.getAll()) {
         DimensionSavedDataManager dimensionsaveddatamanager = new DimensionSavedDataManager(dimensiontype, saveHandlerIn);
         builder.put(dimensiontype, dimensionsaveddatamanager);
         dimensionsaveddatamanager.loadIdCounts();
      }

      this.dataManagers = builder.build();
   }

   @Nullable
   public <T extends WorldSavedData> T get(DimensionType dim, Function<String, T> p_212426_2_, String p_212426_3_) {
      return this.dataManagers.get(dim).getOrLoadData(p_212426_2_, p_212426_3_);
   }

   public void set(DimensionType dim, String dataKey, WorldSavedData savedData) {
      this.dataManagers.get(dim).setData(dataKey, savedData);
   }

   public void save() {
      this.dataManagers.values().forEach(DimensionSavedDataManager::save);
   }

   public int getDataIdForDimension(DimensionType dim, String dataKey) {
      return this.dataManagers.get(dim).getUniqueDataId(dataKey);
   }

   public NBTTagCompound func_208028_a(String p_208028_1_, int p_208028_2_) throws IOException {
      return DimensionSavedDataManager.func_212774_a(this.saveHandler, DimensionType.OVERWORLD, p_208028_1_, p_208028_2_);
   }
}
