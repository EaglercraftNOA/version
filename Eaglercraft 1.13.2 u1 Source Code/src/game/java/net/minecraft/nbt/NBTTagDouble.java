package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagDouble extends NBTPrimitive {
   private double data;

   NBTTagDouble() {
   }

   public NBTTagDouble(double data) {
      this.data = data;
   }

   public void write(DataOutput output) throws IOException {
      output.writeDouble(this.data);
   }

   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(128L);
      this.data = input.readDouble();
   }

   public byte getId() {
      return 6;
   }

   public String toString() {
      return this.data + "d";
   }

   public NBTTagDouble copy() {
      return new NBTTagDouble(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagDouble && this.data == ((NBTTagDouble)p_equals_1_).data;
      }
   }

   public int hashCode() {
      long i = Double.doubleToLongBits(this.data);
      return (int)(i ^ i >>> 32);
   }

   public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
      ITextComponent itextcomponent = (new TextComponentString("d")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponentString(String.valueOf(this.data))).appendSibling(itextcomponent).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getLong() {
      return (long)Math.floor(this.data);
   }

   public int getInt() {
      return MathHelper.floor(this.data);
   }

   public short getShort() {
      return (short)(MathHelper.floor(this.data) & '\uffff');
   }

   public byte getByte() {
      return (byte)(MathHelper.floor(this.data) & 255);
   }

   public double getDouble() {
      return this.data;
   }

   public float getFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }
}
