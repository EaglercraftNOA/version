package net.minecraft.util.text;

public class TextComponentString extends TextComponentBase {
   public static final TextComponentString EMPTY = new TextComponentString("");
   private final String text;

   public TextComponentString(String msg) {
      this.text = msg;
   }

   public String getText() {
      return this.text;
   }

   public String getUnformattedComponentText() {
      return this.text;
   }

   public TextComponentString shallowCopy() {
      return new TextComponentString(this.text);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponentString)) {
         return false;
      } else {
         TextComponentString textcomponentstring = (TextComponentString)p_equals_1_;
         return this.text.equals(textcomponentstring.getText()) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}
