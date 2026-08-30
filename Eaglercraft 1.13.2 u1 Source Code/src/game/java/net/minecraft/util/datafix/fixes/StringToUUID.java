package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.serialization.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.UUID;
import net.lax1dude.eaglercraft.v1_8.EaglerUUIDHelper;
import net.minecraft.util.datafix.TypeReferences;

public class StringToUUID extends DataFix {
   public StringToUUID(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityStringUuidFix", this.getInputSchema().getType(TypeReferences.ENTITY), (p_206344_0_) -> {
         return p_206344_0_.update(DSL.remainderFinder(), (p_206345_0_) -> {
            if (p_206345_0_.get("UUID").flatMap(Dynamic::getStringValue).isPresent()) {
               UUID uuid = UUID.fromString(p_206345_0_.getString("UUID"));
               return p_206345_0_.remove("UUID").set("UUIDMost", p_206345_0_.createLong(EaglerUUIDHelper.getMostSignificantBits(uuid))).set("UUIDLeast", p_206345_0_.createLong(EaglerUUIDHelper.getLeastSignificantBits(uuid)));
            } else {
               return p_206345_0_;
            }
         });
      });
   }
}
