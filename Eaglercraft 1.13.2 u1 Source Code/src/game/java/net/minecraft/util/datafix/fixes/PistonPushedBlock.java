package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.serialization.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.TypeReferences;

public class PistonPushedBlock extends NamedEntityFix {
   public PistonPushedBlock(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "BlockEntityBlockStateFix", TypeReferences.BLOCK_ENTITY, "minecraft:piston");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      Type<?> type = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:piston");
      Type<?> type1 = type.findFieldType("blockState");
      OpticFinder<?> opticfinder = DSL.fieldFinder("blockState", type1);
      Dynamic<?> dynamic = p_207419_1_.get(DSL.remainderFinder());
      int i = dynamic.getInt("blockId");
      dynamic = dynamic.remove("blockId");
      int j = dynamic.getInt("blockData") & 15;
      dynamic = dynamic.remove("blockData");
      Dynamic<?> dynamic1 = BlockStateFlatteningMap.getFixedNBTForID(i << 4 | j);
      Typed<?> typed = type.pointTyped(p_207419_1_.getOps()).orElseThrow(() -> {
         return new IllegalStateException("Could not create new piston block entity.");
      });
      return typed.set(DSL.remainderFinder(), dynamic).set(opticfinder, type1.readTyped(dynamic1).result().map((p_210976_0_) -> {
         return p_210976_0_.getFirst();
      }).orElseThrow(() -> {
         return new IllegalStateException("Could not parse newly created block state tag.");
      }));
   }
}
