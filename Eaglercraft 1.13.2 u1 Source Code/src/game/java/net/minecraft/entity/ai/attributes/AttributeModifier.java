package net.minecraft.entity.ai.attributes;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class AttributeModifier {
   private static final Random random = new Random();
   private final double amount;
   private final int operation;
   private final Supplier<String> name;
   private final UUID id;
   private boolean isSaved = true;

   public AttributeModifier(String nameIn, double amountIn, int operationIn) {
      this(MathHelper.getRandomUUID(random), new NameSupplier(nameIn), amountIn, operationIn);
   }

   public AttributeModifier(UUID idIn, String nameIn, double amountIn, int operationIn) {
      this(idIn, new NameSupplier(nameIn), amountIn, operationIn);
   }

   public AttributeModifier(UUID p_i49578_1_, Supplier<String> p_i49578_2_, double p_i49578_3_, int p_i49578_5_) {
      this.id = p_i49578_1_;
      this.name = p_i49578_2_;
      this.amount = p_i49578_3_;
      this.operation = p_i49578_5_;
      Validate.inclusiveBetween(0L, 2L, (long)p_i49578_5_, "Invalid operation");
   }

   public UUID getID() {
      return this.id;
   }

   public String getName() {
      return this.name.get();
   }

   public int getOperation() {
      return this.operation;
   }

   public double getAmount() {
      return this.amount;
   }

   public boolean isSaved() {
      return this.isSaved;
   }

   public AttributeModifier setSaved(boolean saved) {
      this.isSaved = saved;
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         AttributeModifier attributemodifier = (AttributeModifier)p_equals_1_;
         if (this.id != null) {
            if (!this.id.equals(attributemodifier.id)) {
               return false;
            }
         } else if (attributemodifier.id != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id != null ? this.id.hashCode() : 0;
   }

   public String toString() {
      return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + (String)this.name.get() + '\'' + ", id=" + this.id + ", serialize=" + this.isSaved + '}';
   }

   private static class NameSupplier implements Supplier<String> {
      private final String name;

      private NameSupplier(String name) {
         this.name = name;
      }

      public String get() {
         return this.name;
      }
   }
}
