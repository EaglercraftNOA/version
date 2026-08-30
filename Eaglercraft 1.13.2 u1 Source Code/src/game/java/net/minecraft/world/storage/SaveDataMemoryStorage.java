package net.minecraft.world.storage;

import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SaveDataMemoryStorage extends WorldSavedDataStorage {
   public SaveDataMemoryStorage() {
      super((ISaveHandler)null);
   }

   public int getDataIdForDimension(DimensionType dim, String dataKey) {
      return 0;
   }
}
