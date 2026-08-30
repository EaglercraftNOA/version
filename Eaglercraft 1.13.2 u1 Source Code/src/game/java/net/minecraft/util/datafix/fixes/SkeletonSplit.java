package net.minecraft.util.datafix.fixes;

import com.mojang.serialization.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class SkeletonSplit extends EntityRenameHelper {
   public SkeletonSplit(Schema outputSchema, boolean changesType) {
      super("EntitySkeletonSplitFix", outputSchema, changesType);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String name, Dynamic<?> tag) {
      if (Objects.equals(name, "Skeleton")) {
         int i = tag.getInt("SkeletonType");
         if (i == 1) {
            name = "WitherSkeleton";
         } else if (i == 2) {
            name = "Stray";
         }
      }

      return Pair.of(name, tag);
   }
}
