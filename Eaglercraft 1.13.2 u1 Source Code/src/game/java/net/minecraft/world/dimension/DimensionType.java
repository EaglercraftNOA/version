package net.minecraft.world.dimension;

import java.io.File;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class DimensionType {
   public static final DimensionType OVERWORLD = register("overworld", new DimensionType(1, "", "", OverworldDimension::new));
   public static final DimensionType NETHER = register("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new));
   public static final DimensionType THE_END = register("the_end", new DimensionType(2, "_end", "DIM1", EndDimension::new));
   private final int id;
   private final String suffix;
   private final String directory;
   private final Supplier<? extends Dimension> factory;

   public static void boot() {
   }

   private static DimensionType register(String key, DimensionType type) {
      IRegistry.DIMENSION_TYPE.register(type.id, new ResourceLocation(key), type);
      return type;
   }

   protected DimensionType(int id, String suffixIn, String directoryIn, Supplier<? extends Dimension> factory) {
      this.id = id;
      this.suffix = suffixIn;
      this.directory = directoryIn;
      this.factory = factory;
   }

   public static Iterable<DimensionType> getAll() {
      return IRegistry.DIMENSION_TYPE;
   }

   public int getId() {
      return this.id - 1;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public File getDirectory(File p_212679_1_) {
      return this.directory.isEmpty() ? p_212679_1_ : new File(p_212679_1_, this.directory);
   }

   public VFile2 getDirectory(VFile2 p_212679_1_) {
      return this.directory.isEmpty() ? p_212679_1_ : WorldsDB.newVFile(p_212679_1_, this.directory);
   }

   public Dimension create() {
      return this.factory.get();
   }

   public String toString() {
      return getKey(this).toString();
   }

   @Nullable
   public static DimensionType getById(int id) {
      return IRegistry.DIMENSION_TYPE.get(id + 1);
   }

   @Nullable
   public static DimensionType byName(ResourceLocation nameIn) {
      return IRegistry.DIMENSION_TYPE.get(nameIn);
   }

   @Nullable
   public static ResourceLocation getKey(DimensionType dim) {
      return IRegistry.DIMENSION_TYPE.getKey(dim);
   }
}
