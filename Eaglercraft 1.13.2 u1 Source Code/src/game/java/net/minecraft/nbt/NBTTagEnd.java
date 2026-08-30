package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagEnd implements INBTBase {
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(64L);
   }

   public void write(DataOutput output) throws IOException {
   }

   public byte getId() {
      return 0;
   }

   public String toString() {
      return "END";
   }

   public NBTTagEnd copy() {
      return new NBTTagEnd();
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      return new TextComponentString("");
   }

   public boolean equals(Object p_equals_1_) {
      return p_equals_1_ instanceof NBTTagEnd;
   }

   public int hashCode() {
      return this.getId();
   }
}
