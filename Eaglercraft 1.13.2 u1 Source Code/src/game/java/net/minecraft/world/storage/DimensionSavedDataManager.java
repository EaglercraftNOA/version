package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionSavedDataManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DimensionType dimensionType;
   private Map<String, WorldSavedData> savedDatum = Maps.newHashMap();
   private final Object2IntMap<String> field_212779_d = new Object2IntOpenHashMap<>();
   @Nullable
   private final ISaveHandler saveHandler;

   public DimensionSavedDataManager(DimensionType dim, @Nullable ISaveHandler handler) {
      this.dimensionType = dim;
      this.saveHandler = handler;
      this.field_212779_d.defaultReturnValue(-1);
   }

   @Nullable
   public <T extends WorldSavedData> T getOrLoadData(Function<String, T> factory, String name) {
      WorldSavedData worldsaveddata = this.savedDatum.get(name);
      if (worldsaveddata == null && this.saveHandler != null) {
         try {
            VFile2 file1 = this.saveHandler.getDataFile(this.dimensionType, name);
            if (file1 != null && file1.exists()) {
               worldsaveddata = (WorldSavedData)factory.apply(name);
               worldsaveddata.read(func_212774_a(this.saveHandler, this.dimensionType, name, 1631).getCompound("data"));
               this.savedDatum.put(name, worldsaveddata);
            }
         } catch (Exception exception) {
            LOGGER.error("Error loading saved data: {}", name, exception);
         }
      }

      return (T)worldsaveddata;
   }

   public void setData(String dataIdentifier, WorldSavedData data) {
      this.savedDatum.put(dataIdentifier, data);
   }

   public void loadIdCounts() {
      try {
         this.field_212779_d.clear();
         if (this.saveHandler == null) {
            return;
         }

         VFile2 file1 = this.saveHandler.getDataFile(this.dimensionType, "idcounts");
         if (file1 != null && file1.exists()) {
            DataInputStream datainputstream = new DataInputStream(file1.getInputStream());
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
            datainputstream.close();

            for(String s : nbttagcompound.keySet()) {
               if (nbttagcompound.contains(s, 99)) {
                  this.field_212779_d.put(s, nbttagcompound.getInt(s));
               }
            }
         }
      } catch (Exception exception) {
         LOGGER.error("Could not load aux values", (Throwable)exception);
      }

   }

   public int getUniqueDataId(String key) {
      int i = this.field_212779_d.getInt(key) + 1;
      this.field_212779_d.put(key, i);
      if (this.saveHandler == null) {
         return i;
      } else {
         try {
            VFile2 file1 = this.saveHandler.getDataFile(this.dimensionType, "idcounts");
            if (file1 != null) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();

               for(Entry<String> entry : this.field_212779_d.object2IntEntrySet()) {
                  nbttagcompound.putInt(entry.getKey(), entry.getIntValue());
               }

               DataOutputStream dataoutputstream = new DataOutputStream(file1.getOutputStream());
               CompressedStreamTools.write(nbttagcompound, dataoutputstream);
               dataoutputstream.close();
            }
         } catch (Exception exception) {
            LOGGER.error("Could not get free aux value {}", key, exception);
         }

         return i;
      }
   }

   public static NBTTagCompound func_212774_a(ISaveHandler handler, DimensionType dim, String dataKey, int version) throws IOException {
      VFile2 file1 = handler.getDataFile(dim, dataKey);

      NBTTagCompound nbttagcompound1;
      try (InputStream fileinputstream = file1.getInputStream()) {
         NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
         int i = nbttagcompound.contains("DataVersion", 99) ? nbttagcompound.getInt("DataVersion") : 1343;
         nbttagcompound1 = NBTUtil.update(handler.getFixer(), DataFixTypes.SAVED_DATA, nbttagcompound, i, version);
      }

      return nbttagcompound1;
   }

   public void save() {
      if (this.saveHandler != null) {
         for(WorldSavedData worldsaveddata : this.savedDatum.values()) {
            if (worldsaveddata.isDirty()) {
               this.saveData(worldsaveddata);
               worldsaveddata.setDirty(false);
            }
         }

      }
   }

   private void saveData(WorldSavedData data) {
      if (this.saveHandler != null) {
         try {
            VFile2 file1 = this.saveHandler.getDataFile(this.dimensionType, data.getName());
            if (file1 != null) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();
               nbttagcompound.put("data", data.write(new NBTTagCompound()));
               nbttagcompound.putInt("DataVersion", 1631);
               OutputStream fileoutputstream = file1.getOutputStream();
               CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
               fileoutputstream.close();
            }
         } catch (Exception exception) {
            LOGGER.error("Could not save data {}", data, exception);
         }

      }
   }
}
