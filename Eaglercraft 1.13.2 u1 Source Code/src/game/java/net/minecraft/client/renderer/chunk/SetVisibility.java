package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SetVisibility {
   private static final EnumFacing[] facings = EnumFacing._VALUES;
   private static final int COUNT_FACES = facings.length;
   private final BitSet bitSet = new BitSet(COUNT_FACES * COUNT_FACES);

   public void setManyVisible(Set<EnumFacing> facing) {
      for(EnumFacing enumfacing : facing) {
         for(EnumFacing enumfacing1 : facing) {
            this.setVisible(enumfacing, enumfacing1, true);
         }
      }

   }

   public void setVisible(EnumFacing facing, EnumFacing facing2, boolean value) {
      this.bitSet.set(facing.ordinal() + facing2.ordinal() * COUNT_FACES, value);
      this.bitSet.set(facing2.ordinal() + facing.ordinal() * COUNT_FACES, value);
   }

   public void setAllVisible(boolean visible) {
      this.bitSet.set(0, this.bitSet.size(), visible);
   }

   public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
      return this.bitSet.get(facing.ordinal() + facing2.ordinal() * COUNT_FACES);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(' ');

      for(EnumFacing enumfacing : facings) {
         stringbuilder.append(' ').append(enumfacing.toString().toUpperCase().charAt(0));
      }

      stringbuilder.append('\n');

      for(EnumFacing enumfacing2 : facings) {
         stringbuilder.append(enumfacing2.toString().toUpperCase().charAt(0));

         for(EnumFacing enumfacing1 : facings) {
            if (enumfacing2 == enumfacing1) {
               stringbuilder.append("  ");
            } else {
               boolean flag = this.isVisible(enumfacing2, enumfacing1);
               stringbuilder.append(' ').append((char)(flag ? 'Y' : 'n'));
            }
         }

         stringbuilder.append('\n');
      }

      return stringbuilder.toString();
   }
}
